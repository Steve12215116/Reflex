package org.reflexframework;

import org.reflexframework.lang.IInvokeListener;
import org.reflexframework.lang.LangUtil;

/**
 * android reflex应用。不同应用之间逻辑是一样的，调用api不一样。
 * @author jiangjiang
 *
 */
class AndroidReflexApplication {

	private static final String ROOT_CLASS = "android.view.ViewGroup";
	
	private static final String VIEW_CHANGE_INTERFACE_SET_METHOD = "setOnHierarchyChangeListener";
	
	private static final String VIEW_CHANGE_INTERFACE = "android.view.ViewGroup.OnHierarchyChangeListener";
	
	public boolean run(Object root)
	{
		if(!checkRootView(root))
		{
			return false;
		}
		return observeViewChange(root);
	}
	
	/**
	 * 检查root view是否合法
	 * @param root
	 * @return
	 */
	protected boolean checkRootView(Object root)
	{
		return LangUtil.isObjectClazzAssignedTo(root, ROOT_CLASS);
	}
	
	protected boolean observeViewChange(Object parent)
	{
		return LangUtil.observeInterfaceInvoke(parent, VIEW_CHANGE_INTERFACE_SET_METHOD, VIEW_CHANGE_INTERFACE, new IInvokeListener() {
			
			public void onInvoked(Object source, Class<?> interfaceClazz,
					String method, Object[] args) {
				onViewHierarchyChange(source, method, args);
			}
		});
	}
	
	/**
	 * 视图层次发生改变，有些视图创建了，有些视图被删除了。
	 * @param parent
	 * @param method
	 * @param args
	 */
	protected void onViewHierarchyChange(Object parent, String method, Object[] args)
	{
		
	}
	
	protected void onViewAdded(Object view)
	{
		//root set method, jie kou
	}
	
	protected void onViewRemoved(Object view)
	{
		
	}
}
