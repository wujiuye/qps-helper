package github.wujiuye.qps;

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
        data.currentWindow(); // 确保当前时间的bucket不为空
        long success = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            success += window.success();
        }
        return success;
    }

    @Override
    public long exception() {
        data.currentWindow();  // 确保当前时间的bucket不为空
        long exception = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            exception += window.exception();
        }
        return exception;
    }

    @Override
    public long rt() {
        data.currentWindow();  // 确保当前时间的bucket不为空
        long rt = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            rt += window.rt();
        }
        return rt;
    }

    @Override
    public long minRt() {
        data.currentWindow();  // 确保当前时间的bucket不为空
        long rt = 0;
        List<MetricBucket> list = data.values();
        for (MetricBucket window : list) {
            if (window.minRt() < rt) {
                rt = window.minRt();
            }
        }
        return Math.max(1, rt);
    }

    @Override
    public MetricBucket[] buckets() {
        data.currentWindow();  // 确保当前时间的bucket不为空
        return data.values().toArray(new MetricBucket[0]);
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
    public void addRT(long rt) {
        WindowWrap<MetricBucket> wrap = data.currentWindow();
        wrap.value().addRT(rt);
    }

    /**
     * 获取给定事件的总数
     *
     * @param event 要计算的事件
     * @return 总的计数
     */
    public long getSum(MetricEvent event) {
        data.currentWindow();  // 确保当前时间的bucket不为空
        long sum = 0;
        List<MetricBucket> buckets = data.values();
        for (MetricBucket bucket : buckets) {
            sum += bucket.get(event);
        }
        return sum;
    }

    /**
     * 获取某个事件平均计数
     *
     * @param event 要计算的事件
     * @return 平均值（每秒平均数）
     */
    public double getAvg(MetricEvent event) {
        return getSum(event) / data.getIntervalInSecond();
    }

    @Override
    public double getWindowIntervalInSec() {
        return data.getIntervalInSecond();
    }

    @Override
    public int getSampleCount() {
        return data.getSampleCount();
    }

}
