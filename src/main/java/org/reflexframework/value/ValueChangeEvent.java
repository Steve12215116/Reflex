package org.reflexframework.value;

import org.reflexframework.spi.event.Event;

public class ValueChangeEvent<T> extends Event {

	public static final String TYPE = "valueChanged";
	
	public ValueChangeEvent(Object target, T oldValue, T newValue) {
		super(TYPE, target);
	}

}
