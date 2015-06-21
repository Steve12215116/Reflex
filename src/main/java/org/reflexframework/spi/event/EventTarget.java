package org.reflexframework.spi.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EventTarget implements IEventTarget {

	private Map<String, List<IEventListener<? extends Event>>> listeners;
	
	public <T extends Event> void addEventListener(String eventType, IEventListener<T> listener) {
		if(listeners == null)
		{
			listeners = new HashMap<String, List<IEventListener<? extends Event>>>();
		}
		if(!listeners.containsKey(eventType))
		{
			listeners.put(eventType, new ArrayList<IEventListener<? extends Event>>());
		}
		listeners.get(eventType).add(listener);
	}
	
	protected  void fireEvent(Event event)
	{
		if(listeners == null)
		{
			return;
		}
		if(!listeners.containsKey(event.getType()))
		{
			return;
		}
		List<IEventListener<? extends Event>> list = listeners.get(event.getType());
		for(IEventListener listener : list)
		{
			listener.onEvent(event);
		}
	}

	public <T extends Event> void removeEventListener(String eventType,IEventListener<T> listener) {
		if(listeners == null)
		{
			return;
		}
		if(!listeners.containsKey(eventType))
		{
			return;
		}
		listeners.get(eventType).remove(listener);
	}
}
