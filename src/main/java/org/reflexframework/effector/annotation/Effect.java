package org.reflexframework.effector.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Effect {
	
	/**
	 * 效应对象
	 * @return
	 */
	String target() default "";
	
	/**
	 * 效应具体部位
	 * @return
	 */
	String site();
}
