package org.reflexframework.spi.context;

public interface IReceptBinder {
	
	boolean match(Object view, String name);
	
	void bind(Object view, String stimulation, IStimulationInvokeListener callback);
	
	void unBind(Object view);
}
