package org.reflexframework.transaction;

import org.reflexframework.transaction.annotation.Transactional;

/**
 * 捕获导致事务回滚的异常.
 * @author jiangjiang
 *
 */
public interface IRollbackThrowableAware {
	/**
	 * 捕获到异常，需要回滚，在事务回滚前，调用该方法。
	 * @param transactionName 事务名，在{@link Transactional}里定义，如果没有定义，则取注解所在的方法名。
	 * @param throwable 捕获的异常
	 * 
	 * @return true 如果自己处理回滚;fase,让系统自己处理
	 */
	boolean onRollbackThrowable(TransactionManager transactionManager, String transactionName, Throwable throwable);
}
