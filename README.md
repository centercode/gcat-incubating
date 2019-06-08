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
3. avg|max gc time
4. avg|min gc interval
5. JVM Heap Size
   年轻代，老年代，meta各区的总大小，峰值大小
6. 吞吐量
7. 延迟（平均暂停时间，最大暂停时间）
8. Gc暂停时长分布
9. CMS各阶段耗时
   累计时长、占比
   gc原因占比

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

gc count: yong gc + tenured gc count.