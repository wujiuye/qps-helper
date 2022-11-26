package com.wujiuye.flow.common;

import java.util.ArrayList;
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
        long rt = Long.MAX_VALUE;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            long wrt = window.minRt();
            if (wrt > 0 && wrt < rt) {
                rt = wrt;
            }
        }
        return rt == Long.MAX_VALUE ? 0 : rt;
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
        return rt;
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
    public List<MetricNode> copyCurWindows() {
        List<MetricNode> buckets = new ArrayList<>();
        data.currentWindow();
        List<WindowWrap<MetricBucket>> bucketsWindows = windows();
        for (WindowWrap<MetricBucket> windowWrap : bucketsWindows) {
            if (windowWrap == null) {
                continue;
            }
            MetricNode newBucket = new MetricNode();
            newBucket.setTimestamp(windowWrap.windowStart());
            newBucket.setSuccessCnt(windowWrap.value().success());
            newBucket.setExceptionCnt(windowWrap.value().exception());
            newBucket.setAvgRt(windowWrap.value().rt() / windowWrap.value().success());
            newBucket.setRt(windowWrap.value().rt());
            newBucket.setMinRt(windowWrap.value().minRt());
            newBucket.setMaxRt(windowWrap.value().maxRt());
            buckets.add(newBucket);
        }
        return buckets;
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
