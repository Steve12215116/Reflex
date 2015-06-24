# Reflex
Java GUI 千千万万，并且不断发展，先后有Awt, Swing, Swt, Javafx, Android等等出现，以后可能还会有更多。。。
为了不重新发明轮子，我们需要以不变应万变，就这样，独立于GUI的应用框架-Reflex就诞生了。

为什么需要用Reflex?
 1. Reflex独立于视图，面对不同的GUI，我们可以降低学习成本。最重要的是，在不同的GUI之间,可以重复利用我们的代码，不重新发明轮子，从而让编程是一个积累的快乐旅程。
 2. Reflex应用反射弧模式，把程序分为5个部分： 视图、感受器、效应器、业务中枢、数据模型。每部分职责分配明确、清晰、耦合性低。
 3. Reflex用统一的Annotation 监控视图、更新视图数据、自动注入，减少工作量，提高工作效率。

 在android下监控视图的简单例子： 
 ```	/**
	* 感受对象是  id为bt的视图，行为刺激是： click 事件.
	* @param view
	*/
	@Recept(target="bt", stimulation="android.view.View$OnClickListener")
	private void onBtClicked(View view)
	{
	    helloCenter.changeCount();
	}
```

 更新视图数据的例子：
``` 
	/**
	* 效应对象是  id为text的视图，效应方位是 text属性.
	* @param view
	*/
	@Effect(target="text", site="text")
	public String getHelloText()
	{
		return "hello world " + helloCenter.getCount();
	}
```


目前Reflex还是个雏形，需要更多力量的加入。

下一步计划有：
 1. 支持数据模型的网络访问。
 2. 添加对程序稳定性的支持，主要是本地事务支持。
 3. 对android视图 更友好支持。
 4. JavaFx, Swing的支持...


