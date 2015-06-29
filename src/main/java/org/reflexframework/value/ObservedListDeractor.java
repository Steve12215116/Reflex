package org.reflexframework.value;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.reflexframework.transaction.Status;
import org.reflexframework.transaction.Synchronization;
import org.reflexframework.transaction.TransactionManager;
import org.reflexframework.transaction.TransactionManagerFactory;

public class ObservedListDeractor<T> implements List<T>{

	private List<T>  list;
	
	public ObservedListDeractor(List<T>  list)
	{
		this.list = list;
	}

	
	public boolean add(T e) {
		beforeChange();
		boolean  result = list.add(e);
		afterChange(result);
		return result;
	}

	
	public void add(int index, T element) {
		beforeChange();
		list.add(index, element);
		afterChange(true);
	}

	
	public boolean addAll(Collection<? extends T> c) {
		beforeChange();
		boolean result = list.addAll(c);
		afterChange(result);
		return result;
	}

	
	public boolean addAll(int index, Collection<? extends T> c) {
		beforeChange();
		boolean result = list.addAll(index, c);
		afterChange(result);
		return result;
	}

	
	public void clear() {
		beforeChange();
		list.clear();
		afterChange(true);
	}

	
	public boolean contains(Object o) {
		return list.contains(o);
	}

	
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	
	public T get(int index) {
		return list.get(index);
	}

	
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	
	public boolean isEmpty() {
		return list.isEmpty();
	}

	
	public Iterator<T> iterator() {
		return list.iterator();
	}

	
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	
	public ListIterator<T> listIterator() {
		return list.listIterator();
	}


	public ListIterator<T> listIterator(int index) {
		return list.listIterator(index);
	}

	
	public boolean remove(Object o) {
		beforeChange();
		boolean result =  list.remove(o);
		afterChange(result);
		return result;
	}

	public T remove(int index) {
		beforeChange();
		T result =  list.remove(index);
		afterChange(result != null);
		return result;
	}

	
	public boolean removeAll(Collection<?> c) {
		beforeChange();
		boolean result =  list.removeAll(c);
		afterChange(result);
		return result;
	}

	
	public boolean retainAll(Collection<?> c) {
		beforeChange();
		boolean result =  list.retainAll(c);
		afterChange(result);
		return result;
	}

	
	public T set(int index, T element) {
		beforeChange();
		T result =  list.set(index, element);
		afterChange(true);
		return result;
	}

	
	public int size() {
		return list.size();
	}

	
	public List<T> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	
	public Object[] toArray() {
		return list.toArray();
	}

	
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}
	
	private void beforeChange()
	{
		if(transactionProperty == null)
		{
			TransactionManager manager = getTransactionManager();
			if(manager != null && manager.getStatus() == Status.STATUS_ACTIVE)
			{
				transactionProperty = new TransactionListProperty(this);
				manager.getTransaction().registerSynchronization(transactionProperty);
				transactionProperty.value = this.toArray();
			}
		}
	}
	
	private void afterChange(boolean changed)
	{
		
	}
	
	private TransactionListProperty transactionProperty;
	
	private TransactionManager getTransactionManager()
	{
		return TransactionManagerFactory.getTransactionManager();
	}
	
	private void commited()
	{
		transactionProperty = null;
	}
	
	private void rollback()
	{
		Object result = transactionProperty.value;
		if(result == null)
		{
			list.clear();
		}
		else
		{
			list.clear();
			int count = Array.getLength(result);
			for(int i  = 0; i < count; i++)
			{
				list.add((T)Array.get(result, i));
			}
		}
		transactionProperty = null;
	}
	
	static class TransactionListProperty implements Synchronization
	{
		private ObservedListDeractor owner;
		
		private Object value;
		
		public TransactionListProperty(ObservedListDeractor owner)
		{
			this.owner  = owner;
		}
		
		public void afterCompletion(Status status) {
			switch(status)
			{
				case STATUS_COMMITT:
					owner.commited();
					break;
				default:
					owner.rollback();
					break;
			}
		}
	}
}
