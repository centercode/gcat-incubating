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


1. JVM Heap Size
   年轻代，老年代，meta各区的总大小，峰值大小
2. 吞吐量
3. 延迟（平均暂停时间，最大暂停时间）
4. Gc暂停时长分布
5. CMS各阶段耗时
   累计时长、占比
   gc原因占比

### Example Summary

```
Heap GC Time:
	avg: 72 ms
	max: 25490 ms
	max timestamp: 2019-05-09T02:05:30.342+08(epoch: 1557338730342)
Heap GC Interval:
	avg: 3551 ms
	min: 10 ms
	min timestamp: 2019-05-09T05:06:37.091+08(epoch: 1557349597091)
```