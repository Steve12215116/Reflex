package org.reflexframework.spi.event;

public abstract class Event {
	private Object target;
	
	private String type;
	
	public Event(String type, Object target)
	{
		this.target = target;
		this.type = type;
	}

	public Object getTarget() {
		return target;
	}

	public String getType() {
		return type;
	}
}

