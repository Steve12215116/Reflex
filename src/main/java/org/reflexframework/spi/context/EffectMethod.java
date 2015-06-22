package org.reflexframework.spi.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.reflexframework.center.annotation.Center;
import org.reflexframework.spi.event.BindInvalidateEvent;
import org.reflexframework.spi.event.IEventListener;
import org.reflexframework.spi.event.IEventTarget;

/**
 * 代表一个效应方法。在该方法内部，将监控业务中枢的改变，并自动更新方法的结果给视图。每个效应方法，暂时只支持对一个视图的更改。但是需要支持可以同时监听多个业务对象的改变。
 * @author jiangjiang
 *
 */
public class EffectMethod implements IEventListener<BindInvalidateEvent>{
	
	private String site;
	
	private Method method;
	
	private IBeansCreationAware beans;
	
	private Map<Object, BindStore> bindNames;
	
	private IEffectBinder binder;
	private Object view;
	
	public EffectMethod(IBeansCreationAware beans, String site, Method method)
	{
		this.beans = beans;
		this.site = site;
		this.method  = method;
	}

	/**
	 * 需要更新视图的具体方位。
	 * @return
	 */
	public String getSite() {
		return site;
	}
	
	/**
	 * 更新视图。
	 */
	public void update()
	{
		if(this.binder == null || this.view == null)
		{
			return;
		}
		binder.update(view, site, getValue());
	}
	
	/**
	 * 更新视图。调用该方法，把方法的最新返回值设置给视图
	 * @param binder
	 * @param view  视图
	 */
	public void update(IEffectBinder binder, Object view)
	{
		this.binder = binder;
		this.view = view;
		binder.update(view, site, getValue());
	}
	
	private Object getValue()
	{
		Class<?> clazz = method.getDeclaringClass();
		Object effector = beans.getBean(clazz);
		Object result = null;
		try {
			boolean old = method.isAccessible();
			method.setAccessible(true);
			result = method.invoke(effector);
			method.setAccessible(old);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	
	/**
	 * 监控业务中枢。
	 * @param center  业务中枢
	 * @param bindName 具体监听内容
	 */
	public void bindUpdate(CenterProxy proxy, Object center, String bindName)
	{
		if(center instanceof IEventTarget)
		{
			if(bindNames == null)
			{
				bindNames = new HashMap<Object, BindStore>();
			}
			if(!bindNames.containsKey(center))
			{
				BindStore store = new BindStore(proxy);
				bindNames.put(center, store);
			}
			bindNames.get(center).getNames().add(bindName);
			IEventTarget target =  (IEventTarget)center;
			target.addEventListener(BindInvalidateEvent.TYPE, this);
		}
		
	}

	public void onEvent(BindInvalidateEvent event) {
		if(bindNames == null)
		{
			return;
		}
		List<String> names = bindNames.get(event.getTarget()).getNames();
		if(names == null || !names.contains(event.getBindName()))
		{
			return;
		}
		//主动 update
		update();
	}
	
	/**
	 * 取消该方法对业务中枢的监听.
	 */
	public void unBindUpdate()
	{
		if(bindNames == null)
		{
			return;
		}
		for(Object center : bindNames.keySet())
		{
			IEventTarget target =  (IEventTarget)center;
			target.removeEventListener(BindInvalidateEvent.TYPE, this);
			CenterProxy proxy = bindNames.get(center).getProxy();
			proxy.aware(method);
		}
		bindNames.clear();
		bindNames = null;
	}
	
	static class BindStore {
		private CenterProxy proxy;
		private List<String> names = new ArrayList<String>();
		
		public CenterProxy getProxy() {
			return proxy;
		}

		public List<String> getNames() {
			return names;
		}

		public BindStore(CenterProxy proxy)
		{
			this.proxy = proxy;
		}
		
	}
}
