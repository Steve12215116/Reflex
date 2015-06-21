package org.reflexframework.spi.event;

public interface IEventListener<T extends Event> {
	void onEvent(T event);
}
