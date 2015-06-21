package org.reflexframework.spi.event;

public class BindInvalidateEvent extends Event {

	public static final String TYPE = "bindInValidated";
	
	private String bindName;
	
	public BindInvalidateEvent(Object target, String bindName) {
		super(TYPE, target);
		this.bindName = bindName;
	}

	public String getBindName() {
		return bindName;
	}
	
}
