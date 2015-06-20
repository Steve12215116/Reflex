package org.reflexframework.spi.context;

/**
 * 框架内部用于创建和引用各组件的接口。
 * @author jiangjiang
 *
 */
interface IBeansCreationAware {
	
	Object getReceptor(Class<?> clazz);
	
	Object retreiveReceptor(Class<?> clazz);
}
