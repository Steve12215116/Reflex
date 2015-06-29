package org.reflexframework.transaction;

public class TransactionManagerFactory {
	
	private static TransactionManager manager;
	
	public static TransactionManager getTransactionManager()
	{
		if(manager == null)
		{
			manager = new TransactionManagerImpl();
		}
		return manager;
	}
}
