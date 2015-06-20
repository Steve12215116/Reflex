package org.reflexframework.spi.context;

/**
 * 通知刺激已经发生.
 * @author jiangjiang
 *
 */
public interface IStimulationInvokeListener 
{
	void onInvoked(Object source, String stimultion, Object[] args);
}
