package org.reflexframework.center.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记中枢，系统一般都有多个中枢，负责不同的业务。所有的被中枢标记的类将自动创建，感受器、效应器可以通过名字来引用中枢。可以通过{@link #name()}来设置中枢名称，如果没有设置，则名字为
 * 类名，首字母变为小写。
 * @author jiangjiang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Center {
	/**
	 * 设置中枢名称，如果没有设置，则默认采用类名，首字母变为小写。
	 * @return
	 */
	String value() default "";
}
