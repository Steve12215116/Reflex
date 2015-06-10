package org.reflexframework.lang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class InterfaceInvokeObserver{

	private Object target;
	
	private String setMethod;
	
	private String interfaceClazzName;
	
	private Class<?> interfaceClazz;
	
	private IInvokeListener listener;
	
	public InterfaceInvokeObserver(Object target, String setMethod, String interfaceClazz, IInvokeListener listener)
	{
		this.target = target;
		this.setMethod = setMethod;
		this.interfaceClazzName = interfaceClazz;
		this.listener = listener;
	}
	

	public boolean observe()
	{
		interfaceClazz = LangUtil.findClazz(this.interfaceClazzName);
		if(interfaceClazz == null)
		{
			return false;
		}
		return LangUtil.invokeMethod(target, setMethod, interfaceClazz, Proxy.newProxyInstance(interfaceClazz.getClassLoader(), new Class<?>[]{interfaceClazz}, new   InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
				if(listener != null)
				{
					listener.onInvoked(target, interfaceClazz,method.getName(), args);
				}
				return null;
			}
			
		}));
	}
	
	public void unObserver()
	{
		if(interfaceClazz == null)
		{
			return ;
		}
		LangUtil.invokeMethod(target, setMethod, interfaceClazz, null);
	}
	


}
