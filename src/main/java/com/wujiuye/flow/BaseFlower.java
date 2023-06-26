package com.wujiuye.flow;

import com.wujiuye.flow.common.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 流量统计基类
 *
 * @author wujiuye
 */
public abstract class BaseFlower implements Flower {

    /**
     * 不做序列化
     */
    protected transient Metric metric;

    public BaseFlower(Metric metric) {
        this.metric = metric;
    }

    @Override
    public void incrSuccess(long rt) {
        metric.addSuccess(1);
        metric.addRt(rt);
    }

    @Override
    public long avgRtProp(PropType prop) {
        MetricBucket[] buckets = this.buckets();
        long[] avgRts = new long[buckets.length];
        MetricBucket bucket;
        for (int i = 0; i < buckets.length; i++) {
            bucket = buckets[i];
            avgRts[i] = bucket.rt() / (bucket.success() > 0 ? bucket.success() : 1);
        }
        Arrays.sort(avgRts);
        // 抽样，以bucket的 95% avgRt 作为 95线
        int index = (int) Math.round((float) buckets.length * prop.proportion) - 1;
        return avgRts[index];
    }

    @Override
    public void incrException() {
        metric.addException(1);
    }

    @Override
    public long totalSuccess() {
        return metric.success();
    }

    @Override
    public long totalException() {
        return metric.exception();
    }

    /**
     * 对于HourFlower就是平均每分钟
     * 对于MinuteFlower就是平均每秒
     * 对于SecondFlower就是平均每毫秒
     *
     * @return
     */
    @Override
    public long exceptionAvg() {
        return metric.exception() / getWindowInterval(metric.getWindowInterval());
    }

    /**
     * 对于HourFlower就是平均每分钟
     * 对于MinuteFlower就是平均每秒
     * 对于SecondFlower就是平均每毫秒
     *
     * @return
     */
    @Override
    public float successAvg() {
        float total = metric.success();
        float windows = getWindowInterval(metric.getWindowInterval());
        return total / windows;
    }

    /**
     * 窗口大小单位换算
     *
     * @param windowInterval 窗口大小，单位毫秒
     * @return
     */
    protected abstract long getWindowInterval(long windowInterval);

    /**
     * 平均每个请求的耗时
     *
     * @return
     */
    @Override
    public long avgRt() {
        long successCount = metric.success();
        if (successCount == 0) {
            return 0;
        }
        return metric.rt() / successCount;
    }


    @Override
    public long minRt() {
        return metric.minRt();
    }

    @Override
    public long maxRt() {
        return metric.maxRt();
    }

    @Override
    public List<WindowWrap<MetricBucket>> windows() {
        return new ArrayList<>(metric.windows());
    }

    @Override
    public MetricBucket[] buckets() {
        return metric.buckets();
    }

}
