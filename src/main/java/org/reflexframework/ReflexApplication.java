package org.reflexframework;

import org.reflexframework.app.AndroidReflexApplication;
import org.reflexframework.env.GUIConstants;

/**
 * 启动Reflex应用。启动顺序是这样的：
 * <ul>
 * <li>加载数据模型,驱动业务模型</li>
 * <li>获得顶级视图，监控子视图的创建和销毁</li>
 * <li>扫描感受器和效应器，建立视图和他们之间的关系</li>
 * </ul>
 * @author jiangjiang
 *
 */
public class ReflexApplication {
	
	/**
	 * Reflex应用的入口，在这里给定root视图，如果不希望视图参与，则参数可以为空。
	 * @param rootView root视图，可以为空
	 */ 
	public static void run(Object rootView){
		String clazzName = rootView.getClass().getCanonicalName();
		if(clazzName.indexOf(GUIConstants.PKG_Android) >= 0)
		{
			AndroidReflexApplication app = new AndroidReflexApplication();
			app.run(rootView);
		}
	}
}
