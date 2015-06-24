# Reflex
Java GUI 千千万万，并且不断发展，先后有Awt, Swing, Swt, Javafx, Android等等出现，以后可能还会有更多。。。
为了不重新发明轮子，我们需要以不变应万变，就这样，独立于GUI的应用框架-Reflex就诞生了。


Reflex 目前已经完成的：
<ol>
<li>按照反射弧模式架构整个框架。把程序分为5个部分： 视图、感受器、效应器、业务中枢、数据模型。</li>
<li>关键对象（感受器、效应器、业务中枢）是受框架Managed的，不需要手动创建。。</li>
<li>利用@Autowired 注释，自动注入业务中枢对象。</li>
<li>用统一的Annotation 监控视图、更新视图数据，做到和视图隔离。</li>
 在android下监控视图的简单例子：
	/**
  	* 感受对象是  id为bt的视图，行为刺激是： click 事件.
 	* @param view
  	 */
	@Recept(target="bt", stimulation="android.view.View$OnClickListener")
	private void onBtClicked(View view)
	{
	helloCenter.changeCount();
	}

    更新视图数据的例子：
···
    /**
     * 效应对象是  id为text的视图，效应方位是 text属性.
    * @param view
    */
    @Effect(target="text", site="text")
    public String getHelloText()
    {
         return "hello world " + helloCenter.getCount();
    }
···
<li>业务对象数据发生变化，效应器自动更新数据到视图。</li>
</ol>

目前Reflex还是个雏形，需要更多力量的加入。 下一步计划有：
<ol>
<li>支持数据模型的网络访问。</li>
<li>添加对程序稳定性的支持，主要是本地事务支持。</li>
<li>对android视图 更友好支持。</li>
<li>JavaFx, Swing的支持...</li>
</ol>
