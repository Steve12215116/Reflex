package org.reflexframework.receptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 标记感受器类。所有的感受监听只有在感受器类中定义才会有效。
 * 感受器对象不需要手工创建，当它监听的对象有输入发生的时候，会被自动创建。
 * @author jiangjiang
 * @see Recept
 */
@Target(ElementType.TYPE)
public @interface Receptor {
	
	/**
	 * 通过名字来定义感受目标,可选。如果定义了，则该类中所有的感受监听默认对象都是此对象，除非感受监听重新定义它的目标。
	 * @see Recept
	 */
	String target() default "";
}
