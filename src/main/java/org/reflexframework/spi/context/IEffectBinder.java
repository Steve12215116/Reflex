package org.reflexframework.spi.context;

public interface IEffectBinder {
	
	boolean match(Object view, String name);
	
	void update(Object view, String site, Object value);
}
