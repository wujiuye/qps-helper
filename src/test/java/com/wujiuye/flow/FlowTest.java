package com.wujiuye.flow;

import com.wujiuye.flow.common.MetricBucket;
import com.wujiuye.flow.common.WindowWrap;

import java.util.List;

public class FlowTest {

    static {
        // 初始化指标数据持久化
        MetricPersistencer.init("/tmp");
    }

    public static void main(String[] args) throws InterruptedException {
        FlowHelper flowHelper = new FlowHelper(FlowType.Minute);
        // 注册需要收集指标数据的Flower
        MetricPersistencer.registerFlower("test-resource", flowHelper.getFlow(FlowType.Minute));
        new Thread(() -> {
            int i = 0;
            while (!Thread.interrupted()) {
                if (i++ >= 100) {
                    break;
                }
                try {
                    Thread.sleep(1);
                    flowHelper.incrSuccess(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            while (!Thread.interrupted()) {
                System.out.println("======================================================");
                Flower flower = flowHelper.getFlow(FlowType.Minute);
                System.out.println("总请求数:" + flower.total());
                System.out.println("成功请求数:" + flower.totalSuccess());
                System.out.println("异常请求数:" + flower.totalException());
                System.out.println("平均请求耗时:" + flower.avgRt());
                System.out.println("最大请求耗时:" + flower.maxRt());
                System.out.println("最小请求耗时:" + flower.minRt());
                System.out.println("平均请求成功数:" + flower.successAvg());
                System.out.println("平均请求异常数:" + flower.exceptionAvg());
                System.out.println();
                List<WindowWrap<MetricBucket>> buckets = flower.windows();
                for (WindowWrap<MetricBucket> bucket : buckets) {
                    System.out.print("开始时间戳：" + bucket.windowStart() + "\t");
                    System.out.print("成功数：" + bucket.value().success() + "\t");
                    System.out.print("失败数：" + bucket.value().exception() + "\t");
                    System.out.print("平均耗时：" + (bucket.value().success() > 0 ? (bucket.value().rt() / bucket.value().success()) : 0) + "\t");
                    System.out.print("最大耗时：" + bucket.value().maxRt() + "\t");
                    System.out.print("最小耗时：" + bucket.value().minRt() + "\t");
                    System.out.println();
                }
                System.out.println("======================================================");
                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(Integer.MAX_VALUE);
    }

}
