package org.reflexframework.spi.context;

import java.util.List;

/**
 * 负责管理感受器、业务中枢、效应器的创建、消亡、以及它们之间的引用关系。
 * @author jiangjiang
 *
 */
public interface IBeansContext {
	/**
	 * 搜索所有类，初始化业务中枢,记录感受器、效应器信息。
	 * @param clazz
	 */
	void init(List<Class<?>> clazzes);
	
	/**
	 * 把<code>view</code>和感受器、效应器相连
	 * @param view
	 */
	void connect(Object view, IReceptBinder receptBinder, IEffectBinder effectBinder);	
	
	/**
	 * 断开视图
	 * @param view
	 */
	void disConnect(Object view);
	
}
