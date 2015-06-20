package org.reflexframework.spi.context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflexframework.Initializable;
import org.reflexframework.center.annotation.Autowired;
import org.reflexframework.center.annotation.Center;
import org.reflexframework.effector.annotation.Effector;
import org.reflexframework.receptor.annotation.Recept;
import org.reflexframework.receptor.annotation.Receptor;
import org.reflexframework.spi.lang.LangUtil;
import org.reflexframework.spi.util.StringUtil;

public class BeansContext implements IBeansContext, IBeansCreationAware {

	private List<Class<?>> receptorClazzes;
	
	private List<Class<?>> effectorClazzes;
	
	private Map<String, Object> centers = new HashMap<String, Object>();
	
	/**
	 * 所有的感受器实例
	 */
	private Map<Class<?>, Object> receptorInstances;
	
	
	/**
	 * 名字到感受方法的映射
	 */
	private Map<String, List<ReceptMethod>> targetMethodDicts;
	
	/**
	 * 一个对象和一个刺激唯一决定了它的监听者。
	 */
	private Map<String, CompositeSameObjectStimulationInvokeListener> stimulationInvokeListeners;
	
	public void init(List<Class<?>> clazzes) {	
		for(Class<?> clazz : clazzes)
		{
			//业务中枢
			if(clazz.isAnnotationPresent(Center.class))
			{
				createCenter(clazz);
			}
			else if(clazz.isAnnotationPresent(Receptor.class))
			{
				getReceptorClazzes().add(clazz);
			}
			else if(clazz.isAnnotationPresent(Effector.class))
			{
				getEffectorClazzes().add(clazz);
			}				
		}
		autoWireCenters();
		callInitializedIfNeeded();
	}
	
	/**
	 * 初始化业务中枢
	 * @param clazzes
	 */
	private void createCenter(Class<?> centerClazz)
	{
		Center center  = centerClazz.getAnnotation(Center.class);
		String name = null;
		if(!StringUtil.isEmpty(center.name()))
		{
			name = center.name();
		}
		else
		{	
			name = centerClazz.getSimpleName();
			name = StringUtil.firtLower(name);
		}
		Object instance;
		try {
			instance = centerClazz.newInstance();
			centers.put(name, instance);
		} catch (Exception e)  {
			e.printStackTrace();
		}
	}
	
	/**
	 * 业务中枢之间互相引用
	 */
	private void autoWireCenters()
	{
		for(Object center : centers.values())
		{
			autoWireObjectIntoCenters(center);
		}
	}	
	
	/**
	 * 给指定对象注入业务中枢
	 * @param instance
	 */
	private void autoWireObjectIntoCenters(Object instance)
	{
		Class<?> clazz = instance.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields)
		{
			if(!field.isAnnotationPresent(Autowired.class))
			{
				continue;
			}
			String name = field.getName();
			if(!centers.containsKey(name))
			{
				continue;
			}
			LangUtil.setField(instance, field, centers.get(name));
		}
	}
	
	private void callInitializedIfNeeded()
	{
		for(Object center : centers.values())
		{
			if(Initializable.class.isAssignableFrom(center.getClass()))
			{
				((Initializable)center).onInitialized();
			}
		}
	}
	
	
	public void connect(Object view, IReceptBinder receptBinder,IEffectBinder effectBinder) {
		connectEffector(view, effectBinder);
		connectReceptor(view, receptBinder);
	}



	public void disConnect(Object view) {
		
	}
	/**
	 * 连接感受器。首先找到该视图相关的所有效应器，如果没有创建，则创建，并且自动注入业务中枢，然后建立关联。
	 * @param view 视图
	 * @param binder 建立关联的接口
	 */
	private void connectReceptor(Object view, IReceptBinder binder)
	{
		if(targetMethodDicts == null)
		{
			targetMethodDicts = new HashMap<String, List<ReceptMethod>>();
			List<Class<?>> receptorClazzes = this.getReceptorClazzes();
			for(Class<?> clazz : receptorClazzes)
			{
				scanTargetAndMethod(targetMethodDicts, clazz);
			}
		}
		for(String name : targetMethodDicts.keySet())
		{
			if(binder.match(view, name))
			{
				List<ReceptMethod> descriptors = targetMethodDicts.get(name);
				for(ReceptMethod descriptor : descriptors)
				{
					binder.bind(view, descriptor.getStimulation(), getStimulationInvokeListener(view, descriptor));
				}
			}
		}
	}
	
	private void connectEffector(Object view, IEffectBinder binder)
	{
		
	}
	

	public Object getReceptor(Class<?> clazz) {
		if(receptorInstances == null)
		{
			receptorInstances = new HashMap<Class<?>, Object>();
		}
		if(!receptorInstances.containsKey(clazz))
		{
			Object value = null;
			try {
				value = clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			autoWireObjectIntoCenters(value);
			receptorInstances.put(clazz, value);
		}
		return receptorInstances.get(clazz);
	}

	public Object retreiveReceptor(Class<?> clazz) {
		if(receptorInstances == null)
		{
			return null;
		}
		return receptorInstances.get(clazz);
	}
	
	
	/**
	 * 搜索该类中所有方法，建立target和方法的关系
	 * @param dict
	 * @param receptorClazz
	 * @return 
	 */
	private void scanTargetAndMethod(Map<String, List<ReceptMethod>> dict, Class<?> receptorClazz)
	{
		Receptor receptor= receptorClazz.getAnnotation(Receptor.class);
		String defaultTarget  = receptor.target();		
		Method[] methods = receptorClazz.getDeclaredMethods();
		String target = null;
		List<ReceptMethod>  descriptors = null;
		for(Method method : methods)
		{
			if(!method.isAnnotationPresent(Recept.class))
			{
				continue;
			}
			Recept recept = method.getAnnotation(Recept.class);
			target = recept.target();
			
			target = target == null || target.length() == 0 ? defaultTarget : target;
			
			if(!dict.containsKey(target))
			{
				dict.put(target, new ArrayList<ReceptMethod>());
			}
			descriptors = dict.get(target);		
			descriptors.add(new ReceptMethod(this, recept.stimulation(), method));
		}
	}
	
	/**
	 * 相同的视图和刺激，获得相同的刺激监听器。
	 * @return
	 */
	private IStimulationInvokeListener getStimulationInvokeListener(Object view, ReceptMethod method)
	{
		CompositeSameObjectStimulationInvokeListener listener = null;
		if(stimulationInvokeListeners == null)
		{
			stimulationInvokeListeners = new HashMap<String, CompositeSameObjectStimulationInvokeListener>();
		}
		String key = createKey(view, method.getStimulation());
		if(!stimulationInvokeListeners.containsKey(key))
		{
			listener = new CompositeSameObjectStimulationInvokeListener();
			stimulationInvokeListeners.put(key, listener);
			listener.add(method);
		}
		listener = stimulationInvokeListeners.get(key);
		return listener;
	}
	
	private String createKey(Object target, String stimulation)
	{
		return target.hashCode() + "#" + stimulation;
	}
	
	
	private List<Class<?>> getReceptorClazzes()
	{
		if(receptorClazzes == null)
		{
			receptorClazzes = new ArrayList<Class<?>>();
		}
		return receptorClazzes;
	}
	
	private List<Class<?>> getEffectorClazzes()
	{
		if(effectorClazzes == null)
		{
			effectorClazzes = new ArrayList<Class<?>>();
		}
		return effectorClazzes;
	}

}
