package org.reflexframework.spi.context;

import java.lang.reflect.Method;

public class ReceptMethod implements IStimulationInvokeListener{

	private String stimulation;
	
	
	
	public String getStimulation() {
		return stimulation;
	}

	public Method getMethod() {
		return method;
	}

	public Object getSource() {
		return source;
	}

	private Method method;
	
	private Object source;
	
	private IBeansCreationAware beansAware;
	
	public ReceptMethod(IBeansCreationAware beansAware, String stimulation, Method method)
	{
		this.beansAware = beansAware;
		this.stimulation = stimulation;
		this.method = method;
	}

	public void onInvoked(Object source, String stimultion, Object[] args) {
		Class<?> clazz = method.getDeclaringClass();
		Object receptor = beansAware.getBean(clazz);
		try {
			boolean old = method.isAccessible();
			method.setAccessible(true);
			method.invoke(receptor, args);
			method.setAccessible(old);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
