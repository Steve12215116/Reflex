package org.reflexframework.transaction;

class TransactionManagerImpl implements TransactionManager{

	private Status status = Status.STATUS_NO_TRANSACTION;
	
	private TransactionImpl transcation;
	

	public void begin() {
		status = Status.STATUS_ACTIVE;
		transcation = new TransactionImpl();
	}

	
	public void commit() {
		if(status != Status.STATUS_ACTIVE)
		{
			return;
		}
		status = Status.STATUS_COMMITT;
		transcation.deliverStatus(status);
		reset();
	}

	
	public void rollback() {
		if(status != Status.STATUS_ACTIVE)
		{
			return;
		}
		status = Status.STATUS_ROLLBACK;
		transcation.deliverStatus(status);
		reset();
	}


	public Transaction getTransaction() {
		return transcation;
	}


	public Status getStatus() {
		return status;
	}
	
	private void reset()
	{
		status = Status.STATUS_NO_TRANSACTION;
		transcation =null;
	}

}
