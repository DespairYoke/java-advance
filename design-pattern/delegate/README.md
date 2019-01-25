### 具体做法

## 1.创建一个员工干活的接口 Target

## 2.分别创建两个干活的员工 ATarget,BTarget 然后用这两个员工去实现Target接口

## 3.创建一个可以管理AB员工的leader去实现Target，然后去给A和B员工委派具体的任务

## 4.大boss给leader下达具体的需要干活的指令（DelegateTest），由于前面leader已经将任务委派给A和B，所以，这里虽然是大boss给leader下达命令，但是具体干活的却是A和B
