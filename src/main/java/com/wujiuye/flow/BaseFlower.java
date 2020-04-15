package com.wujiuye.flow;

import com.wujiuye.flow.common.Metric;
import com.wujiuye.flow.common.MetricBucket;
import com.wujiuye.flow.common.WindowWrap;

import java.util.ArrayList;
import java.util.List;

/**
 * 流量统计基类
 *
 * @author wujiuye
 */
public abstract class BaseFlower implements Flower {

    /**
     * 不做序列化
     */
    private transient Metric metric;

    public BaseFlower(Metric metric) {
        this.metric = metric;
    }

    @Override
    public void incrSuccess(long rt) {
        metric.addSuccess(1);
        metric.addRt(rt);
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
    public long successAvg() {
        return metric.success() / getWindowInterval(metric.getWindowInterval());
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
