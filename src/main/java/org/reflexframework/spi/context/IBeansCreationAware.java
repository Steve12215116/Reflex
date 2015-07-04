package org.reflexframework.spi.context;

import java.lang.reflect.Method;

/**
 * 框架内部用于创建和引用各组件的接口。
 * @author jiangjiang
 *
 */
public interface IBeansCreationAware {
	
	/**
	 * 根据类查找对应的bean,如果没有，则创建。
	 * @param clazz
	 * @return
	 */
	Object getBean(Class<?> clazz);
	
	Object retrieveBean(Class<?> clazz);
	
	EffectMethod retrieveEffectMethod(Method method);
}
