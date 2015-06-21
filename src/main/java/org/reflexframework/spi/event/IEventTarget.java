package org.reflexframework.spi.event;

public interface IEventTarget {
	<T extends Event> void addEventListener(String eventType, IEventListener<T> listener);
	
	<T extends Event> void removeEventListener(String eventType, IEventListener<T> listener);
}
