package org.reflexframework.transaction;

/**
 * 本地事务，保证数据的完整性。
 * @author jiangjiang
 *
 */
public interface TransactionManager {
	
	/**
	 * 开始一个事务。开始后，一定要提交或者回滚事务，整个事务才算结束。
	 */
	void begin();
	
	/**
	 * 提交事务。
	 */
	void commit();
	
	/**
	 * 回滚。
	 */
	void rollback();
	
	/**
	 * 获取当前事务，只有到事务开始后，结束前，才能得到事务。
	 * @return
	 */
	Transaction getTransaction();
	
	/**
	 * 当前状态.
	 * @return
	 */
	Status getStatus();
}
