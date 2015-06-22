package org.reflexframework.spi.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.reflexframework.center.annotation.Bindable;
import org.reflexframework.effector.annotation.Effector;
import org.reflexframework.spi.lang.LangUtil;

class CenterProxy implements InvocationHandler{
	
	private Object center;
	
	private Object proxy;
	
	private static final String PROXY_CLASS_NAME = CenterProxy.class.getName();
	
	private Map<String, Boolean> pathCovered = new HashMap<String, Boolean>();
	
	private IBeansCreationAware beans;
	
	public CenterProxy(IBeansCreationAware beans, Object center)
	{
		this.center = center;
		this.beans = beans;
		Class<?> centerClazz = center.getClass();
		proxy = Proxy.newProxyInstance(centerClazz.getClassLoader(), centerClazz.getInterfaces(), this);
	}
	
	public Object getProxy()
	{
		return proxy;
	}
	
	/**
	 * 添加业务中枢对效应方法的再次感知能力
	 * @param method
	 */
	public void aware(Method method)
	{
		String key = method.getDeclaringClass().getName() + "#"+ method.getName();
		pathCovered.remove(key);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
	{
		Object result = method.invoke(center, args);
		Method realMethod = center.getClass().getMethod(method.getName(), method.getParameterTypes());
		if(!realMethod.isAnnotationPresent(Bindable.class))
		{
			return result;
		}
		String bindName = realMethod.getAnnotation(Bindable.class).name();
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		int length = elements.length;
		boolean lookEffectMethod = false;
		Class<?> effectorClazz = null;
		String effectorClazzName = null;
		for(int i = 0; i < length; i++)
		{
			StackTraceElement element= elements[i];
			if( element.getClassName().equals(PROXY_CLASS_NAME))
			{
				i += 3;
				element = elements[i];
				effectorClazz = LangUtil.findClazz(element.getClassName());
				String key = element.getClassName() + "#"+ element.getMethodName();
				if(pathCovered.containsKey(key))
				{
					break;
				}
				pathCovered.put(key, true);
				if(!effectorClazz.isAnnotationPresent(Effector.class))
				{
					break;
				}
				effectorClazzName = effectorClazz.getName();
				lookEffectMethod = true;
			}
			if(!lookEffectMethod)
			{
				continue;
			}
			//保持一直在effector内
			if(!effectorClazzName.equals(element.getClassName()))
			{
				break;
			}
			//一个业务方法可能对应多个效应的改变。
			//找到effect method,然后监控该方法的改变
			String methodName = element.getMethodName();
			try
			{
				Method mm = effectorClazz.getMethod(methodName);
				if(mm.isAnnotationPresent(org.reflexframework.effector.annotation.Effect.class))
				{
					//TODO
					EffectMethod effectMethod = beans.retrieveEffectMethod(mm);
					if(effectMethod != null)
					{
						effectMethod.bindUpdate(this, center, bindName);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		}
		return result;
	}
}
