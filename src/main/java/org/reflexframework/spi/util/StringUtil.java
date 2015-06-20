package org.reflexframework.spi.util;

public final class StringUtil {

	public static String firtUpper(String str)
	{
		return str.substring(0,1).toUpperCase() + str.substring(1);
	}
	
	public static String firtLower(String str)
	{
		return str.substring(0,1).toLowerCase() + str.substring(1);
	}
	
	public static boolean isEmpty(String str)
	{
		return str == null || str.trim().length() <= 0;
	}
	
}
