# qps-helper
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.github.wujiuye/qps-helper/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.wujiuye/qps-helper)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

* [版本更新历史](https://github.com/wujiuye/qps-helper/releases)

通用的qps、tps统计工具包

### 添加依赖配置

jdk1.8项目使用此依赖
```xml
<!-- https://mvnrepository.com/artifact/com.github.wujiuye/qps-helper -->
<dependency>
    <groupId>com.github.wujiuye</groupId>
    <artifactId>qps-helper</artifactId>
    <version>1.1.2-RELEASE</version>
</dependency>
```

jdk1.7项目使用此依赖
```xml
<!-- https://mvnrepository.com/artifact/com.github.wujiuye/qps-helper -->
<dependency>
    <groupId>com.github.wujiuye</groupId>
    <artifactId>qps-helper</artifactId>
    <version>JDK7.1.1.2-RELEASE</version>
</dependency>
```

### 使用qps-helper统计接口的QPS

```java
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DemoController {
    
    private FlowHelper flowHelper = new FlowHelper(FlowType.HOUR);

    @GetMapping("/test")
    public ApiResponse testApi() {
        try{
            long startTime = TimeUtil.currentTimeMillis();
            // 业务逻辑
            //    ......
            // 计算耗时
            long rt = TimeUtil.currentTimeMillis() - startTime;
            flowHelper.incrSuccess(rt);
            return ApiResponse.ok();
        }catch (Exception e){
            flowHelper.incrException();
            return ApiResponse.error();
        }
    }

}
```

### 统计每秒钟

```java
public class Main{
    private FlowHelper flowHelper = new FlowHelper(FlowType.Second);
}
```

输出最近一秒钟统计
```java
public class Main{
    public static void print(){
        Flower flower = flowHelper.getFlow(FlowType.Second);
        System.out.println("总请求数:"+flower.total());
        System.out.println("成功请求数:"+flower.totalSuccess());
        System.out.println("异常请求数:"+flower.totalException());
        System.out.println("平均请求耗时:"+flower.avgRt());
        System.out.println("最大请求耗时:"+flower.maxRt());
        System.out.println("最小请求耗时:"+flower.minRt());
        System.out.println("平均请求成功数(每毫秒):"+flower.successAvg());
        System.out.println("平均请求异常数(每毫秒):"+flower.exceptionAvg());
        System.out.println();
    }
}
```

### 统计每分钟

```java
public class Main{
    private FlowHelper flowHelper = new FlowHelper(FlowType.Minute);
}
```

输出最近一分钟统计
```java
public class Main{
    public static void print(){
        Flower flower = flowHelper.getFlow(FlowType.Minute);
        System.out.println("总请求数:"+flower.total());
        System.out.println("成功请求数:"+flower.totalSuccess());
        System.out.println("异常请求数:"+flower.totalException());
        System.out.println("平均请求耗时:"+flower.avgRt());
        System.out.println("最大请求耗时:"+flower.maxRt());
        System.out.println("最小请求耗时:"+flower.minRt());
        System.out.println("平均请求成功数(每秒钟):"+flower.successAvg());
        System.out.println("平均请求异常数(每秒钟):"+flower.exceptionAvg());
        System.out.println();
    }
}
```

### 统计每小时

```java
public class Main{
    private FlowHelper flowHelper = new FlowHelper(FlowType.HOUR);
}
```

输出最近一小时统计
```java
public class Main{
    public static void print(){
        Flower flower = flowHelper.getFlow(FlowType.HOUR);
        System.out.println("总请求数:"+flower.total());
        System.out.println("成功请求数:"+flower.totalSuccess());
        System.out.println("异常请求数:"+flower.totalException());
        System.out.println("平均请求耗时:"+flower.avgRt());
        System.out.println("最大请求耗时:"+flower.maxRt());
        System.out.println("最小请求耗时:"+flower.minRt());
        System.out.println("平均请求成功数(每分钟):"+flower.successAvg());
        System.out.println("平均请求异常数(每分钟):"+flower.exceptionAvg());
        System.out.println();
    }
}
```

### 组合统计

```java
public class Main{
    // 可任意组合
    private FlowHelper flowHelper = new FlowHelper(FlowType.HOUR,FlowType.Minute,FlowType.Second);
}
```

输出最近一小时、一分钟、一毫秒的统计
```java
public class Main{
    public static void print(){
        // 获取每秒钟统计
        Flower secondFlower = flowHelper.getFlow(FlowType.Second);
        // 获取每分钟统计
        Flower minuteFlower = flowHelper.getFlow(FlowType.Minute);
        // 获取每小时统计
        Flower hourFlower = flowHelper.getFlow(FlowType.HOUR);
    }
}
```

### 获取详情

以统计每分钟数据为例
```java
public class Main{
    private FlowHelper flowHelper = new FlowHelper(FlowType.Minute);
    
    public static void print(){
       // 获取每分钟统计
       Flower flower = flowHelper.getFlow(FlowType.Minute);
       List<WindowWrap<MetricBucket>> buckets = flower.windows();
       for (WindowWrap<MetricBucket> bucket : buckets) {
           System.out.print("开始时间戳：" + bucket.windowStart() + "\t");
           System.out.print("成功数：" + bucket.value().success() + "\t");
           System.out.print("失败数：" + bucket.value().exception() + "\t");
           System.out.print("平均耗时：" + (bucket.value().rt() / bucket.value().success()) + "\t");
           System.out.print("最大数：" + bucket.value().maxRt() + "\t");
           System.out.print("最小数：" + bucket.value().minRt() + "\t");
           System.out.println();
       }
       System.out.println();
    }
}
```

### 指标数据持久化

1、在main方法中调用MetricPersistencer的init方法初始化指标数据持久化功能
```
MetricPersistencer.init("/tmp");
```
* 其中参数为用于存放指标数据文件的目录。

2、给MetricPersistencer注册需要持久化的资源Flower
```
MetricPersistencer.registerFlower("test-resource", flowHelper.getFlow(FlowType.Minute));
```
* 参数1：资源名称
* 参数2：Flower，目前仅支持MinuteFlower

### A&Q

* 1、平均请求数的统计有问题？
平均请求数的计算：如果统计一分钟的qps，那么平均就是当前成功请求数/60秒。\
所以要准确的话，前一分钟是准确的，当前分钟是不准确的。
