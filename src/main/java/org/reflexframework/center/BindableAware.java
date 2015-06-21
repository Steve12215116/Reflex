package org.reflexframework.center;

import org.reflexframework.spi.event.BindInvalidateEvent;
import org.reflexframework.spi.event.EventTarget;

public class BindableAware extends EventTarget {
	
	protected final void invalidateBind(String name)
	{
		fireEvent(new BindInvalidateEvent(this, name));
	}
}
