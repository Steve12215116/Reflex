package org.reflexframework.spi.lang;

/**
 * 调用方法后的返回结果。有可能没有调用成功，成功后，可能有结果，也可能没有结果，如setter方法。
 * @author jiangjiang
 *
 */
public class InvokeResult {
	private Object result;
	
	private boolean successful;
	
	public InvokeResult(Object result)
	{
		this.successful = true;
		this.result = result;
	}
	
	public InvokeResult(boolean successful)
	{
		this.successful = successful;
	}

	public Object getResult() {
		return result;
	}

	public boolean isSuccessful() {
		return successful;
	}
}
