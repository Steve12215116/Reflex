package org.reflexframework.spi.context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.reflexframework.spi.util.StringUtil;
import org.reflexframework.transaction.IRollbackThrowableAware;
import org.reflexframework.transaction.TransactionManager;
import org.reflexframework.transaction.TransactionManagerFactory;
import org.reflexframework.transaction.annotation.Transactional;

public class ReceptMethod implements IStimulationInvokeListener{

	private String stimulation;
	
	
	
	public String getStimulation() {
		return stimulation;
	}

	public Method getMethod() {
		return method;
	}

	public Object getSource() {
		return source;
	}

	private Method method;
	
	private Object source;
	
	private IBeansCreationAware beansAware;
	
	public ReceptMethod(IBeansCreationAware beansAware, String stimulation, Method method)
	{
		this.beansAware = beansAware;
		this.stimulation = stimulation;
		this.method = method;
	}

	public void onInvoked(Object source, String stimultion, Object[] args) {
		Class<?> clazz = method.getDeclaringClass();
		Object receptor = beansAware.getBean(clazz);
		Transactional transactional = null;
		TransactionManager transactionManager = null;
		boolean rollback = false;
		boolean userHandle =false;
		if(method.isAnnotationPresent(Transactional.class))
		{
			transactional = method.getAnnotation(Transactional.class);
			transactionManager = TransactionManagerFactory.getTransactionManager();
		}
		try {
			if(transactionManager != null)
			{
				transactionManager.begin();
			}
			boolean old = method.isAccessible();
			method.setAccessible(true);
			method.invoke(receptor, args);
			method.setAccessible(old);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			rollback = true;
			Throwable innerException = e.getTargetException();
			e.printStackTrace();
			if(transactionManager != null && receptor instanceof IRollbackThrowableAware)
			{
				String transactionName = transactional.value();
				if(StringUtil.isEmpty(transactionName))
				{
					transactionName = method.getName();
				}
				userHandle = ((IRollbackThrowableAware)receptor).onRollbackThrowable(transactionManager, transactionName, innerException);
			}
		}
		finally
		{
			if(!userHandle && transactionManager != null)
			{
				if(rollback)
				{
					transactionManager.rollback();
				}
				else
				{
					transactionManager.commit();
				}		
			}
		}

	}
}
