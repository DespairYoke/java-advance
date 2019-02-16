### 面试题
#### hashmap结构；什么对象能做为key
不可变对象。
因为：HashMap用Key的哈希值来存储和查找键值对。当插入一个Entry时，HashMap会计算Entry Key的哈希值。Map会根据这个哈希值把Entry插入到相应的位置。

查找时，HashMap通过计算Key的哈希值到特定位置查找这个Entry。而可变对象可能会是hash值发生改变。

#### hashtable,concurrentHashMap,hashtable比较

* hashMap是线程不安全的 hashtable是线程安全的

* hashMap key和value都可以为null hashtable不行，所以hashMap不能使用get()判断null，应使用containsKey()

* Hashtable中采用的锁机制是一次锁住整个hash表，从而同一时刻只能由一个线程对其进行操作;而ConcurrentHashMap中则是一次锁住一个桶。
ConcurrentHashMap默认将hash表分为16个桶，诸如get,put,remove等常用操作只锁当前需要用到的桶。这样，原来只能一个线程进入，
现在却能同时有16个写线程执行，并发性能的提升是显而易见的。

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