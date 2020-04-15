package com.wujiuye.flow.common;

import java.util.List;

/**
 * 数组度量器
 *
 * @author wujiuye
 */
public class ArrayMetric implements Metric {

    private final LeapArray<MetricBucket> data;

    public ArrayMetric(int sampleCount, int intervalInMs) {
        this.data = new BucketLeapArray(sampleCount, intervalInMs);
    }

    @Override
    public long success() {
        // 确保当前时间的bucket不为空
        data.currentWindow();
        long success = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            success += window.success();
        }
        return success;
    }

    @Override
    public long exception() {
        // 确保当前时间的bucket不为空
        data.currentWindow();
        long exception = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            exception += window.exception();
        }
        return exception;
    }

    @Override
    public long rt() {
        // 确保当前时间的bucket不为空
        data.currentWindow();
        long rt = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            rt += window.rt();
        }
        return rt;
    }

    @Override
    public long minRt() {
        // 确保当前时间的bucket不为空
        data.currentWindow();
        long rt = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            if (window.minRt() < rt || rt == 0) {
                rt = window.minRt();
            }
        }
        return Math.max(1, rt);
    }

    @Override
    public long maxRt() {
        // 确保当前时间的bucket不为空
        data.currentWindow();
        long rt = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            if (window.maxRt() > rt) {
                rt = window.maxRt();
            }
        }
        return Math.max(1, rt);
    }

    @Override
    public MetricBucket[] buckets() {
        // 确保当前时间的bucket不为空
        data.currentWindow();
        return data.values().toArray(new MetricBucket[0]);
    }

    @Override
    public List<WindowWrap<MetricBucket>> windows() {
        return data.list();
    }

    @Override
    public void addException(int count) {
        WindowWrap<MetricBucket> wrap = data.currentWindow();
        wrap.value().addException(count);
    }

    @Override
    public void addSuccess(int count) {
        WindowWrap<MetricBucket> wrap = data.currentWindow();
        wrap.value().addSuccess(count);
    }

    @Override
    public void addRt(long rt) {
        WindowWrap<MetricBucket> wrap = data.currentWindow();
        wrap.value().addRt(rt);
    }

    /**
     * 获取给定事件的总数
     *
     * @param event 要计算的事件
     * @return 总的计数
     */
    public long getSum(MetricEvent event) {
        // 确保当前时间的bucket不为空
        data.currentWindow();
        long sum = 0;
        List<MetricBucket> buckets = data.values();
        for (MetricBucket bucket : buckets) {
            sum += bucket.get(event);
        }
        return sum;
    }

    @Override
    public long getWindowInterval() {
        return data.getIntervalInMs();
    }

    @Override
    public int getSampleCount() {
        return data.getSampleCount();
    }

}
