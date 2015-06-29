package org.reflexframework.transaction;

import java.util.ArrayList;
import java.util.List;

class TransactionImpl implements Transaction {

	private List<Synchronization> listeners;
	

	public void registerSynchronization(Synchronization sync) {
		if(listeners == null)
		{
			listeners = new ArrayList<Synchronization>();
		}
		if(listeners.contains(sync))
		{
			return;
		}
		listeners.add(sync);
	}
	
	protected void deliverStatus(Status status)
	{
		if(listeners == null)
		{
			return;
		}
		if(status == Status.STATUS_ACTIVE)
		{
			return;
		}
		for(Synchronization sync : listeners)
		{
			sync.afterCompletion(status);
		}
	}
}
