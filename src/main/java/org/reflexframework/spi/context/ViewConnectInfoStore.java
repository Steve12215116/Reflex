package org.reflexframework.spi.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记录视图连接时的一些信息,以备以后之需。
 * @author jiangjiang
 *
 */
public class ViewConnectInfoStore {
	
	private IEffectBinder effectBinder;
	
	private IReceptBinder receptBinder;
	
	private Object view;
	
	private List<EffectMethod> effectMethods;
	
	private List<ReceptMethod> receptMethods;
	
	private Map<ReceptMethod, IStimulationInvokeListener> method2InvokeListeners;
	
	public ViewConnectInfoStore(Object view, IReceptBinder receptBinder, IEffectBinder effectBinder)
	{
		this.view = view;
		this.receptBinder = receptBinder;
		this.effectBinder = effectBinder;
	}

	public IEffectBinder getEffectBinder() {
		return effectBinder;
	}

	public IReceptBinder getReceptBinder() {
		return receptBinder;
	}
	
	/**
	 * 记录感受信息
	 */
	public void storeRecept(ReceptMethod method,  IStimulationInvokeListener invokeListener)
	{
		if(receptMethods == null)
		{
			receptMethods = new ArrayList<ReceptMethod>();
		}
		if(receptMethods.contains(method))
		{
			return;
		}
		receptMethods.add(method);
		if(method2InvokeListeners == null)
		{
			method2InvokeListeners = new HashMap<ReceptMethod, IStimulationInvokeListener>();
		}
		method2InvokeListeners.put(method, invokeListener);
	}
	
	/**
	 * 记录效应方法
	 * @param method
	 */
	public void storeEffect(EffectMethod method)
	{
		if(effectMethods == null)
		{
			effectMethods = new ArrayList<EffectMethod>();
		}
		if(effectMethods.contains(method))
		{
			return;
		}
		effectMethods.add(method);
	}
	
	/**
	 * 断开连接，并销毁本对象
	 */
	public void unBindAndDispose()
	{
		if(receptMethods != null)
		{
			for(ReceptMethod method : receptMethods)
			{
				IStimulationInvokeListener invokeListener = method2InvokeListeners.get(method);		
				if(invokeListener instanceof CompositeSameObjectStimulationInvokeListener)
				{
					((CompositeSameObjectStimulationInvokeListener)invokeListener).remove(method); 
				}
				receptBinder.unBind(this.view, method.getStimulation(), invokeListener);
			}
			receptMethods.clear();
			method2InvokeListeners.clear();
			
			receptMethods = null;
			method2InvokeListeners = null;
		}
		
		if(effectMethods != null)
		{
			for(EffectMethod method : effectMethods)
			{
				method.unBindUpdate();
			}
			effectMethods.clear();
			effectMethods = null;
		}
		
		//dispose
		this.view = null;
		this.effectBinder = null;
		this.receptBinder = null;
	}
	
	
}
