package org.reflexframework.receptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 感受器的感受入口，在方法级别监听来自视图的输入。一般视图的动作的输出，有两种办法：
 * <ul>
 * <li>派发事件</li>
 * <li>调用接口</li>
 * </ul>
 * 如果想监听它们，用户只需要通过<code>stimulation</code>来指定事件或者接口的方法，就可以啦。系统会根据<code>stimulation</code>自动建立和<code>target</code>的关系。
 * <br/>
 * <code>stimulation</code>是字符串，它的格式:[类全路径.]名称[#方法名]
 * <br/>
 * 如果事件或接口是GUI框架自带的，则类路径可以省略，否则，如果是自定义的，需要提供类路径，或者统一设置。
 * 名称可以是事件或者接口名称，优先认为是事件，如果找不了对应事件，才会认为是接口。如果名称是接口，并且接口有多个方法，需要指定方法名。
 * 
 * @author jiangjiang
 *
 */

@Target(ElementType.METHOD)
public @interface Recept {
	/**
	 * 通过名字来定义感受目标，可以为空。如果为空，则所有目标的对应输入都会被捕获
	 * @return
	 */
	String target();
	
	/**
	 * 刺激定义。格式:[类全路径.]名称[#方法名]<br/>
	 * 如果事件或接口是GUI框架自带的，则类路径可以省略，否则，如果是自定义的，需要提供类路径，或者统一设置。
	 * 名称可以是事件或者接口名称，优先认为是事件，如果找不了对应事件，才会认为是接口。如果名称是接口，并且接口有多个方法，需要指定方法名。
	 * @return
	 */
	String stimulation();
	
}
