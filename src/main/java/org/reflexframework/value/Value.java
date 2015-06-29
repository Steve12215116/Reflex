package org.reflexframework.value;


/**
 * 使用该类来设置数据,有两个特性：
 * <ol>
 * <li>支持事务特性，必要的时候可以回滚。</li>
 * <li>通知属性改变</li>
 * </ol>
 * @author jiangjiang
 *
 * @param <T> 属性具体类型。
 */
public class Value<T> extends TransactionalDataBase
{
	private T value;
	
	public Value()
	{
	}
	
	public Value(T value)
	{
		this.value = value;
	}
	
	/*
	 * 设值
	 */
	public void set(T value)
	{
		beforeChange();
		boolean changed = changed(this.value, value);
		T old = this.value;
		this.value = value;
		afterChange(changed, old, value);
	}
	
	/**
	 * 获取值
	 * @return
	 */
	public T get()
	{
		return value;
	}

	@Override
	protected void commited() {
		
	}

	@Override
	protected void rollback(Object value) {
		T converted = value == null ? null : (T)value;
		boolean changed = changed(this.value, converted);
		T old = this.value;
		this.value = converted;
		afterChange(changed,  old, converted);
	}

	@Override
	protected Object beginTransaction() {
		return this.value;
	}


	protected void afterChange(boolean changed, T oldValue, T newValue) {
		if(!changed)
		{
			return;
		}
		this.fireEvent(new ValueChangeEvent<T>(this, oldValue, newValue));
	}
	
	private boolean changed(Object old, Object value)
	{
		boolean changed = true;
		if(value != null && value.equals(old))
		{
			changed = false;
		}
		else if(old != null && old.equals(value))
		{
			changed = false;
		}
		else if(value == old)
		{
			changed = false;
		}
		return changed;
	}
}
