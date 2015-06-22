package org.reflexframework.spi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflexframework.spi.context.BeansContext;
import org.reflexframework.spi.context.IBeansContext;
import org.reflexframework.spi.context.IEffectBinder;
import org.reflexframework.spi.context.IReceptBinder;
import org.reflexframework.spi.context.IStimulationInvokeListener;
import org.reflexframework.spi.lang.IInvokeListener;
import org.reflexframework.spi.lang.LangUtil;
import org.reflexframework.spi.util.StringUtil;

/**
 * android reflex应用。不同应用之间逻辑是一样的，调用api不一样。
 * @author jiangjiang
 *
 */
public class AndroidReflexApplication implements IReceptBinder, IEffectBinder{

	private static final String ROOT_CLASS = "android.view.ViewGroup";
	
	private static final String CONTAINER_CLASS = "android.view.ViewGroup";
	
	private static final String VIEW_CHANGE_INTERFACE_SET_METHOD = "setOnHierarchyChangeListener";
	
	private static final String VIEW_CHANGE_INTERFACE = "android.view.ViewGroup$OnHierarchyChangeListener";
	
	private static final String METHOD_VIEW_CHILD_ADD = "onChildViewAdded";
	
	private static final String METHOD_VIEW_CHILD_REMOVE = "onChildViewRemoved";
	
	private static final String METHOD_VIEW_GET_CHILDREN_COUNT = "getChildCount";
	
	private static final String METHOD_VIEW_GET_CHILD_AT = "getChildAt";
	

	private IBeansContext beansContext;
	
	
	private Map<String, IStimulationInvokeListener> stimulationListeners = new HashMap<String, IStimulationInvokeListener>();;
	
	public AndroidReflexApplication()
	{
		beansContext = new BeansContext();
	}
	
	public boolean run(Object root)
	{
		List<Class<?>> clazzes = getUserApplicationClazzes(root);
		beansContext.init(clazzes);;
		//1,创建所有的业务中枢
		//2,建立全局感受器
	//	initReceptorContext(root);
		if(!checkRootView(root))
		{
			return false;
		}
		return observeViewChange(root);
	}
	
	/**
	 * 检查root view是否合法
	 * @param root
	 * @return
	 */
	protected boolean checkRootView(Object root)
	{
		return LangUtil.isObjectClazzAssignedTo(root, ROOT_CLASS);
	}
	
	protected boolean observeViewChange(Object parent)
	{
		beansContext.connect(parent, this, this);
		observeChildren(parent);
		return LangUtil.observeInterfaceInvoke(parent, VIEW_CHANGE_INTERFACE_SET_METHOD, VIEW_CHANGE_INTERFACE, new IInvokeListener() {
			
			public void onInvoked(Object source, Class<?> interfaceClazz,
					String method, Object[] args) {
				onViewHierarchyChange(source, method, args);
			}
		}, null);
	}
	
	/**
	 * 监控<code>parent</code>视图已有的孩子们的变化
	 * @param parent
	 */
	private void observeChildren(Object parent)
	{
		if(!isContainer(parent))
		{
			return;
		}
		Iterable<Object> children = getChildren(parent);
		if(children == null)
		{
			return;
		}
		for (Object child : children)
		{
			observeViewChange(child);
		}
	}
	
	/**
	 * 检查该视图的类型，是否是容器
	 * @param parent
	 * @return
	 */
	protected boolean isContainer(Object parent)
	{
		return LangUtil.isObjectClazzAssignedTo(parent, CONTAINER_CLASS);
	}
	
	/**
	 * 获取视图容器的孩子
	 * @param parent
	 * @return
	 */
	protected Iterable<Object> getChildren(Object parent)
	{
		int count = LangUtil.get(parent,METHOD_VIEW_GET_CHILDREN_COUNT, null);
		if(count <= 0)
		{
			return null;
		}
		List<Object> children = new ArrayList<Object>();
		for(int i = 0; i < count; i++)
		{
			Object child = LangUtil.get(parent,METHOD_VIEW_GET_CHILD_AT, new Class<?>[] {int.class}, i);
			children.add(child);
		}
		return children;
	}
	
	/**
	 * 视图层次发生改变，有些视图创建了，有些视图被删除了。
	 * @param parent
	 * @param method
	 * @param args
	 */
	protected void onViewHierarchyChange(Object parent, String method, Object[] args)
	{
		Object child = args[1];
		if(method.equals(METHOD_VIEW_CHILD_ADD))
		{
			onViewAdded(args[0], child);
			observeViewChange(child);
		}
		else if(method.equals(METHOD_VIEW_CHILD_REMOVE))
		{
			onViewRemoved(args[0], child);
		}
	}
	
	protected void onViewAdded(Object parent, Object view)
	{
		stimulate(parent, "onChildAdded", view);
	//	receptorContext.sendSimulation("", "onChildAdded", parent, view);
	}
	
	protected void onViewRemoved(Object parent, Object view)
	{
		beansContext.disConnect(view);
		stimulate(parent, "onChildRemoved", view);
		//receptorContext.sendSimulation("", "onChildRemoved", parent, view);
	}

	private void stimulate(Object view, String stimulation, Object ... args)
	{
		String key = createKey(view, stimulation);
		if(!stimulationListeners.containsKey(key))
		{
			return;
		}
		stimulationListeners.get(key).onInvoked(view, stimulation, args);
	}

	/**
	 * 视图是否可以用<code>descriptor</code>表示。该<code>descriptor</code>和 {@link org.reflexframework.receptor.annotation.Recept}的<code>target</code>相对应
	 * @param view
	 * @param descriptor
	 * @return
	 */
	public boolean match(Object view, String name) {
		//全局监听
		if(StringUtil.isEmpty(name))
		{
			return true;
		}
		int id = LangUtil.get(view, "getId", null, null);
		int idByName = getViewId(view, name);
		return id == idByName;
	}

	public void bind(Object view, final String stimulation,IStimulationInvokeListener callback) {
		String key = createKey(view, stimulation);
		stimulationListeners.put(key, callback);
		IInvokeListener invokeListener = new IInvokeListener() {		
			public void onInvoked(Object source, Class<?> interfaceClazz,
					String method, Object[] args) {
				stimulate(source, stimulation, args);
			}
		};
		apply(view, stimulation, invokeListener);
	}

	public void unBind(Object view, String stimulation,IStimulationInvokeListener callback) {
		String key = createKey(view, stimulation);
		stimulationListeners.remove(key);
		apply(view, stimulation, null);
	}
	
	
	private void apply(Object view, String stimulation, IInvokeListener  invokeListener)
	{
		int index = stimulation.lastIndexOf('#');
		String methodFilter = null;
		String s1 = stimulation;
		if(index > 0)
		{
			methodFilter = s1.substring(index + 1);
			s1 = s1.substring(0, index);
		}
		index = s1.lastIndexOf('$');
		if(index < 0)
		{
			index = s1.lastIndexOf('.');
		}
		String clazzName = null;
		String setterName = null;
		if(index > 0)
		{
			setterName = s1.substring(index + 1);
			clazzName = s1;
		}
		else
		{
			setterName = StringUtil.firtUpper(s1);
		}
		if(clazzName != null)
		{
			List<String> methodFilters = null;
			if(!StringUtil.isEmpty(methodFilter))
			{
				methodFilters = new ArrayList<String>();
				methodFilters.add(methodFilter);
			}
			boolean result = LangUtil.observeInterfaceInvoke(view, "set" + setterName, clazzName, invokeListener, methodFilters);
			if(!result)
			{
				result = LangUtil.observeInterfaceInvoke(view, "add" + setterName, clazzName, invokeListener, methodFilters);
			}
		}
	}
	
	public void update(Object view, String site, Object value) {
		String setterName = "set" + StringUtil.firtUpper(site);	
		LangUtil.invokeMethod(view, setterName, value);
	}
	
	private String createKey(Object view, String stimulation)
	{
		return stimulation + "#" + view.hashCode();
	}
	
	
	private List<Class<?>> getUserApplicationClazzes(Object root)
	{
		String path = System.getProperty("app.path");
		String pkg = System.getProperty("user.pkg");
		return scanUserClazz(path, pkg);
	}

	/**
	 * 获取用户程序所有类和接口。
	 * @param packageName 包名。
	 * @param subPackage 是否搜索子包
	 * @return
	 */
	private  List<Class<?>> scanUserClazz(String appPath, String packagePrefix)
	{
		 try {
			 Class<?> dexFileClazz = LangUtil.findClazz("dalvik.system.DexFile");
			 if(dexFileClazz == null)
			 {
				 return null;
			 }
			 Object file = dexFileClazz.getConstructor(String.class).newInstance(appPath);
			 if(file == null)
			 {
				 return null;
			 }
			 List<Class<?>> list = new ArrayList<Class<?>>();
			 Enumeration<String> iter = LangUtil.get(file, "entries", null);
			 while (iter.hasMoreElements()) {
		            String s = iter.nextElement();
		            if(!s.startsWith(packagePrefix))
		            {
		            	continue;
		            }
		            list.add(LangUtil.findClazz(s));
		        }
			 return list;
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return null;
		
	}

	private int getViewId(Object view, String idString)
	{
		Object context = LangUtil.get(view, "getContext", null);
		context =LangUtil.get(context, "getApplicationContext", null);
		String name = LangUtil.get(context, "getPackageName", null);
		try {
			Class<?> clazz = LangUtil.findClazz(name + ".R$id");
			Field field = clazz.getDeclaredField(idString);
			return field.getInt(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
