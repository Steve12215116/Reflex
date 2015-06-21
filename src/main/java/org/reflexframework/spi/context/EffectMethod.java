package org.reflexframework.spi.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflexframework.spi.event.BindInvalidateEvent;
import org.reflexframework.spi.event.IEventListener;
import org.reflexframework.spi.event.IEventTarget;

public class EffectMethod implements IEventListener<BindInvalidateEvent>{
	
	private String site;
	
	private Method method;
	
	private IBeansCreationAware beans;
	
	private Map<Object, List<String>> bindNames;
	
	private IEffectBinder binder;
	private Object view;
	public EffectMethod(IBeansCreationAware beans, String site, Method method)
	{
		this.beans = beans;
		this.site = site;
		this.method  = method;
	}

	public String getSite() {
		return site;
	}
	
	public void update()
	{
		if(this.binder == null || this.view == null)
		{
			return;
		}
		binder.update(view, site, getValue());
	}
	
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
	
	public void bindUpdate(Object center, String bindName)
	{
		if(center instanceof IEventTarget)
		{
			if(bindNames == null)
			{
				bindNames = new HashMap<Object, List<String>>();
			}
			if(!bindNames.containsKey(center))
			{
				bindNames.put(center, new ArrayList<String>());
			}
			bindNames.get(center).add(bindName);
			IEventTarget target =  (IEventTarget)center;
			target.addEventListener(BindInvalidateEvent.TYPE, this);
		}
		
	}

	public void onEvent(BindInvalidateEvent event) {
		if(bindNames == null)
		{
			return;
		}
		List<String> names = bindNames.get(event.getTarget());
		if(names == null || !names.contains(event.getBindName()))
		{
			return;
		}
		//主动 update
		update();
	}
}
