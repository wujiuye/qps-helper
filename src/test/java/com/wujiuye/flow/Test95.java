package com.wujiuye.flow;

import java.util.Random;

/**
 * Created by ZengShilin on 2023/6/13 8:06 PM
 */
public class Test95 {

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        FlowHelper flowHelper = new FlowHelper(FlowType.FifteenMinute);
        // 注册需要收集指标数据的Flower
        MetricPersistencer.registerFlower("test-resource", flowHelper.getFlow(FlowType.FifteenMinute));
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 200 * 60 * 10000; i++) {
                try {
                    long startTime = System.currentTimeMillis();
                    Thread.sleep(random.nextInt(60 + i));
                    if (i % 5 == 0) {
                        flowHelper.incrException();
                    } else {
                        flowHelper.incrSuccess(System.currentTimeMillis() - startTime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 2 * 60; i++) {
                System.out.println("======================================================");
                Flower flower = flowHelper.getFlow(FlowType.FifteenMinute);
                System.out.println("总请求数:" + flower.total());
//                System.out.println("成功请求数:" + flower.totalSuccess());
//                System.out.println("异常请求数:" + flower.totalException());
                System.out.println("平均请求耗时:" + flower.avgRt());
                System.out.println("最大请求耗时:" + flower.maxRt());
                System.out.println("最小请求耗时:" + flower.minRt());
                System.out.println("成功请求数:" + flower.totalSuccess());
                System.out.println("失败请求数:" + flower.totalException());
                System.out.println("平均请求异常数:" + flower.exceptionAvg());
                System.out.println("99线:" + flower.avgRtProp(PropType.PROP_99));
                System.out.println();
//                List<WindowWrap<MetricBucket>> buckets = flower.windows();
//                for (WindowWrap<MetricBucket> bucket : buckets) {
//                    System.out.print("开始时间戳：" + bucket.windowStart() + "\t");
//                    System.out.print("成功数：" + bucket.value().success() + "\t");
//                    System.out.print("失败数：" + bucket.value().exception() + "\t");
//                    System.out.print("平均耗时：" + MathUtil.divide(bucket.value().rt(), bucket.value().success()) + "\t");
//                    System.out.print("最大耗时：" + bucket.value().maxRt() + "\t");
//                    System.out.print("最小耗时：" + bucket.value().minRt() + "\t");
//                    System.out.println();
//                }
                System.out.println("======================================================");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    public void test(){

    }
}
