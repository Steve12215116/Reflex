package org.reflexframework.spi.lang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

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
	

	public boolean observe(final List<String> methodsFilters)
	{
		interfaceClazz = LangUtil.findClazz(this.interfaceClazzName);
		if(interfaceClazz == null)
		{
			return false;
		}
		InvokeResult result =  LangUtil.invokeMethod(target, setMethod, new Class<?>[]{interfaceClazz}, Proxy.newProxyInstance(interfaceClazz.getClassLoader(), new Class<?>[]{interfaceClazz}, new   InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
				if(methodsFilters != null && methodsFilters.contains(method.getName()))
				{
					return null;
				}
				if(listener != null)
				{
					listener.onInvoked(target, interfaceClazz,method.getName(), args);
				}
				return null;
			}
			
		}));
		return result.isSuccessful();
	}
	
	public void unObserver()
	{
		if(interfaceClazz == null)
		{
			return ;
		}
		LangUtil.invokeMethod(target, setMethod, new Class<?>[]{interfaceClazz}, null);
	}
	


}
