package org.reflexframework;

/**
 * 如果感受器、效应器、业务中枢实现了该接口，则在它们实例化，并且注入了引用变量后，<code>initialized</code>方法被调用。
 * @author jiangjiang
 *
 */
public interface Initializable {
	
	/**
	 *构造完成，并且被注入了引用变量后，该方法被调用。
	 */
	void onInitialized();
}
