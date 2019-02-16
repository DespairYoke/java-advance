### 面试题
#### hashmap结构；什么对象能做为key
不可变对象。
因为：HashMap用Key的哈希值来存储和查找键值对。当插入一个Entry时，HashMap会计算Entry Key的哈希值。Map会根据这个哈希值把Entry插入到相应的位置。

查找时，HashMap通过计算Key的哈希值到特定位置查找这个Entry。而可变对象可能会是hash值发生改变。

#### hashtable,concurrentHashMap,hashtable比较

* hashMap是线程不安全的 hashtable是线程安全的

* hashMap key和value都可以为null hashtable不行，所以hashMap不能使用get()判断null，应使用containsKey()

jdk 1.8后 HashMap的设计采用`数组+链表+红黑树`的形式。
什么时候扩容？

    - 1 当前容量超过阈值

    - 2 当链表中元素个数超过默认设定（8个），当数组的大小还未超过64的时候，此时进行数组的扩容，如果超过则将链表转化成红黑树

什么时候链表转化为红黑树？（上面已经提到了）

    - 当数组大小已经超过64并且链表中的元素个数超过默认设定（8个）时，将链表转化为红黑树

* 1 Hashtable中采用的锁机制是一次锁住整个hash表，从而同一时刻只能由一个线程对其进行操作,但是效率低

* concurrentHashMap在java7以前使用分段锁，java8放弃了分段锁

java7之前的结构图
![java7](./image/concurrenthashmap_java7.png)
这时只要锁住Segment下的hash表。



#### JAVA中的几种基本数据类型是什么，各自占用多少字节。

| 类型 | 存储需求|bit 数|取值范围|备注|
|----- |------ | ------ | ---| ----|
|int | 4字节 | 4*8 | -2147483648~2147483647 | 即 (-2)的31次方 ~ (2的31次方) - 1|
|short	|2字节|	2*8	|-32768~32767	|即 (-2)的15次方 ~ (2的15次方) - 1|
|long	|8字节|	8*8	|	|即 (-2)的63次方 ~ (2的63次方) - 1|
|byte|	1字节|	1*8|	-128~127	|即 (-2)的7次方 ~ (2的7次方) - 1|
|float|	4字节|	4*8	|	|float 类型的数值有一个后缀 F（例如：3.14F）|
|double|	8字节|	8*8	|	|没有后缀 F 的浮点数值（例如：3.14）默认为 double|
|boolean|	1字节|	1*8	|true、false|
|char|	2字节	|2*8	|	|Java中，只要是字符，不管是数字还是英文还是汉字，都占两个字节。|

#### String类能被继承吗，为什么。

不能，为了String类被final关键字修饰。

#### String，Stringbuffer，StringBuilder的区别。

* String：字符串常量，字符串长度不可变。每次重新赋值时，会生成新的对象。不适合使用于频繁修改场景。

* StringBuffer 字符串变量（线程安全）。如果要频繁对字符串内容进行修改且考虑安全，最好使用StringBuffer。

* StringBuilder 字符串变量（非线程安全）。如果要频繁对字符串内容进行修改且考虑效率问题，最好使用StringBuilder。