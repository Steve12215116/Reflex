package org.reflexframework.transaction.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记事务，只能标记在感受方法上，其他方法无效。被事务标记的方法，如果执行过程中发生了异常，则数据自动回滚到方法执行前。
 * @author jiangjiang
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
	/**
	 * 为事务命名
	 * @return
	 */
	String value() default "";
}
