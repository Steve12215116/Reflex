package org.reflexframework.spi.context;

import java.util.ArrayList;
import java.util.List;


public class CompositeSameObjectStimulationInvokeListener implements
		IStimulationInvokeListener {

	private List<IStimulationInvokeListener> methods = new ArrayList<IStimulationInvokeListener>();
	
	public void onInvoked(Object source, String stimultion, Object[] args) {
		for(IStimulationInvokeListener method : methods)
		{
			method.onInvoked(source, stimultion, args);
		}
	}
	
	public void add(IStimulationInvokeListener method)
	{
		if(methods.contains(method))
		{
			return;
		}
		methods.add(method);
	}
	
}
