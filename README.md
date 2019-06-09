# gcat
meaning [gc cat], a java gc log analyser, currently support CMS + ParNew gc collector.

### Build

```
mvn package
```

### Usage

```
java -cp  gcat.jar io.gcat.Gcat {gc.log}
```

### Measure


1. gc duration
2. gc count
3. avg|max gc pause time
4. avg|min gc interval
5. JVM Heap Size
   年轻代，老年代，meta各区的总大小，峰值大小
6. 吞吐量
7. 延迟（平均暂停时间，最大暂停时间）
8. Gc暂停时长分布
9. CMS各阶段耗时
   累计时长、占比
10. gc原因占比

example output:

```
Heap GC Time:
	avg: 72 ms
	max: 25490 ms(at 2019-05-09T02:05:30.342+08, epoch: 1557338730342)
Heap GC Interval:
	avg: 3551 ms
	min: 10 ms(at 2019-05-09T05:06:37.091+08, epoch: 1557349597091)
```

### CMS+ParNew algorithm

gc duration: duration between first valid gc log timestamp to the last valid gc log timestamp.

gc count: yong gc + old gc count.



### Reference

> [Minor GC、Major GC和Full GC之间的区别](http://www.importnew.com/15820.html)
>
> [Major GC和Full GC的区别是什么？触发条件呢？](https://www.zhihu.com/question/41922036)
>
> [Java之CMS GC的7个阶段](https://zhanjia.iteye.com/blog/2435266)
>
> [快速解读GC日志](https://blog.csdn.net/renfufei/article/details/49230943)
>
> [Java GC 日志格式理解小结](https://blog.csdn.net/FIRE_TRAY/article/details/51397905)
>
> [GC 日志格式](https://www.jianshu.com/p/4c6ecfd6f15f)
>
> [Understanding the Java Garbage Collection Log](https://dzone.com/articles/understanding-garbage-collection-log)
>
> [Understanding Garbage Collection Logs](https://plumbr.io/blog/garbage-collection/understanding-garbage-collection-logs)
>
> [浅谈 G1 GC 日志格式](https://my.oschina.net/dabird/blog/710444)
>
> [Useful JVM Flags – Part 8 (GC Logging)](https://blog.codecentric.de/en/2014/01/useful-jvm-flags-part-8-gc-logging/)
>
> [JVM GC 日志详解](https://juejin.im/post/5c80b0f451882532cd57b541)