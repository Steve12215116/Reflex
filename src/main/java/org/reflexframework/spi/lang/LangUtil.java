package org.reflexframework.spi.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 提供一些语言级别的工具方法
 * @author jiangjiang
 *
 */
public class LangUtil {
	
	/**
	 * 赋予<code>target</code>指定接口，监控该接口的调用情况
	 * @param target 赋予接口的对象
	 * @param setMethod  赋予接口的方法
	 * @param interfaceClazz 接口类型
	 * @param listener  接口被调用是的监听器
	 */
	public static boolean observeInterfaceInvoke(Object target, String setMethod, String interfaceClazz, IInvokeListener listener)
	{
		InterfaceInvokeObserver observer = new InterfaceInvokeObserver(target, setMethod, interfaceClazz, listener);
		return observer.observe();
	}
	
	/**
	 * 根据类名找类，如果没有找到，返回null
	 * @param clazz 类名
	 * @return
	 */
	public static Class<?> findClazz(String clazz)
	{
		try {
			return Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据方法名，调用指定方法
	 * @param target
	 * @param methodName
	 * @param paramClazz
	 * @param value
	 * @return
	 */
	public static InvokeResult invokeMethod(Object target, String methodName, Class<?>[] paramClazzs, Object... paramValues)
	{
		try {
			Method method = target.getClass().getMethod(methodName, paramClazzs);
			boolean oldAccess = method.isAccessible();
			method.setAccessible(true);
			Object result = method.invoke(target, paramValues);
			method.setAccessible(oldAccess);
			return new InvokeResult(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new InvokeResult(false);
	}
	
	/**
	 * 给定对象是否可以转换成<code>clazzName</code>指定的类。如果对象为空，或找到不类名所指定的类，直接返回<code>false</code>
	 * @return
	 */
	public static boolean isObjectClazzAssignedTo(Object object, String clazzName)
	{
		if(object == null)
		{
			return false;
		}
		Class<?> clazz = findClazz(clazzName);
		if(clazz == null)
		{
			return false;
		}
		return clazz.isAssignableFrom(object.getClass());
	}
	
	/**
	 * 调用指定对象上的getter方法，获取返回对象
	 * @return
	 */
	public  static  <T> T get(Object object, String getMethod, Class<?>[] paramClazzs, Object... paramValues)
	{
		InvokeResult result = invokeMethod(object, getMethod, paramClazzs, paramValues);
		if(!result.isSuccessful())
		{
			return null;
		}
		return (T)result.getResult();
	}
	
	public static void setField(Object object,  Field field, Object value)
	{
		boolean oldAcs = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(object, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		field.setAccessible(oldAcs);
	}
	
}
