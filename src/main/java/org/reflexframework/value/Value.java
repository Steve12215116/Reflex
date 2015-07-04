package org.reflexframework.value;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.reflexframework.effector.annotation.Effector;
import org.reflexframework.spi.context.BeansContextFactory;
import org.reflexframework.spi.context.EffectMethod;
import org.reflexframework.spi.context.IBeansCreationAware;
import org.reflexframework.spi.lang.LangUtil;


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
	
	
	private List<EffectMethod> effectMethods;
	
	private IBeansCreationAware beans;
	
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
		if(changed && effectMethods != null)
		{
			for(EffectMethod method : effectMethods)
			{
				method.update();
			}
		}
	}
	
	/**
	 * 获取值
	 * @return
	 */
	public T get()
	{
		List<EffectMethod> methods = findCaller();
		if(methods != null && !methods.isEmpty())
		{
			if(effectMethods == null)
			{
				effectMethods = new ArrayList<EffectMethod>();
			}
			for(EffectMethod method : methods)
			{
				if(!effectMethods.contains(method))
				{
					effectMethods.add(method);
				}
			}
		}
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
	
	
	protected List<EffectMethod> findCaller()
	{
		if(beans == null)
		{
			beans =(IBeansCreationAware)BeansContextFactory.getBeansContext();
		}
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		int length = elements.length;
		Class<?> effectorClazz = null;
		List<EffectMethod> methods = null;
		for(int i = 0; i < length; i++)
		{
			StackTraceElement element= elements[i];
			Class<?> clazz = LangUtil.findClazz(element.getClassName());
			if(clazz == null)
			{
				continue;
			}
			if(effectorClazz == null)
			{
				if(!clazz.isAnnotationPresent(Effector.class))
				{
					continue;
				}
				effectorClazz = clazz;
			}
			//保持一直在effector内
			else if(!effectorClazz.equals(clazz))
			{
				break;
			}
			//一个业务方法可能对应多个效应的改变。
			//找到effect method,然后监控该方法的改变
			String methodName = element.getMethodName();
			try
			{
				Method mm =  effectorClazz.getDeclaredMethod(methodName);
				if(mm.isAnnotationPresent(org.reflexframework.effector.annotation.Effect.class))
				{
					if(methods == null)
					{
						methods = new ArrayList<EffectMethod>();
					}
					EffectMethod effectMethod = beans.retrieveEffectMethod(mm);
					if(!methods.contains(effectMethod))
					{
						methods.add(effectMethod);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		}
		return methods;
	}
}
