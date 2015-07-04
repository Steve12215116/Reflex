package org.reflexframework.value;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.reflexframework.effector.annotation.Effector;
import org.reflexframework.spi.context.EffectMethod;
import org.reflexframework.spi.event.EventTarget;
import org.reflexframework.spi.lang.LangUtil;
import org.reflexframework.transaction.Status;
import org.reflexframework.transaction.Synchronization;
import org.reflexframework.transaction.TransactionManager;
import org.reflexframework.transaction.TransactionManagerFactory;

abstract class TransactionalDataBase extends EventTarget{

	private TransactionSyncListener transactionSyncListener;
	
	protected abstract void commited();
	
	protected abstract void rollback(Object value);
	
	/**
	 * 子类需要在数据改变前调用该方法
	 */
	protected final void beforeChange()
	{
		if(transactionSyncListener != null)
		{
			return;
		}
		TransactionManager manager = TransactionManagerFactory.getTransactionManager();
		if(manager != null && manager.getStatus() == Status.STATUS_ACTIVE)
		{
			transactionSyncListener = new TransactionSyncListener(this);
			transactionSyncListener.set(beginTransaction());
			manager.getTransaction().registerSynchronization(transactionSyncListener);
		}
	}
	
	/**
	 * 返回在事务开始的时候需要记录的数据，以用于回滚。
	 * @return
	 */
	protected abstract Object beginTransaction();
	
	
	
	class TransactionSyncListener implements Synchronization {
		
		private Object value;
		
		private TransactionalDataBase owner;
		
		public TransactionSyncListener(TransactionalDataBase owner)
		{
			this.owner  = owner;
		}
		
		void set(Object value)
		{
			this.value = value;
		}


		public void afterCompletion(Status status) {
			switch(status)
			{
				case STATUS_COMMITT:
					owner.commited();
					owner.transactionSyncListener = null;
					break;
				default:
					owner.rollback(this.value);
					owner.transactionSyncListener = null;
					break;
			}
		}
	}

}
