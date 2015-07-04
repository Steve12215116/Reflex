package org.reflexframework.spi.context;

public final class BeansContextFactory {

	private static IBeansContext context;
	
	public static IBeansContext getBeansContext()
	{
		if(context == null)
		{
			context = new BeansContext();
		}
		return context;
	}
}
