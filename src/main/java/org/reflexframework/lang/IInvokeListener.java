package org.reflexframework.lang;

/**
 * 监听接口调用。
 * @author jiangjiang
 *
 */
public interface IInvokeListener {
	/**
	 * <code>interfaceClazz</code>接口被调用。
	 * @param source 接口被赋予的对象
	 * @param interfaceClazz 接口类型
	 * @param method 接口方法名
	 * @param args  接口方法被调用参数
	 */
	void onInvoked(Object source, Class<?> interfaceClazz, String method, Object[] args);
}
