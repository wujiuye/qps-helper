package com.wujiuye.flow.common;

/**
 * 一段时间内的度量数据
 *
 * @author wujiuye
 */
public class MetricBucket {

    /**
     * 存储各事件的计数，比如异常总数、请求总数等
     */
    private final LongAdder[] counters;
    /**
     * 这段时间内的最小耗时
     */
    private volatile long minRt = Long.MAX_VALUE;
    /**
     * 这段时间内的最大耗时
     */
    private volatile long maxRt = Long.MIN_VALUE;

    public MetricBucket() {
        // 初始化数组
        MetricEvent[] events = MetricEvent.values();
        this.counters = new LongAdder[events.length];
        for (MetricEvent event : events) {
            counters[event.ordinal()] = new LongAdder();
        }
    }

    public long get(MetricEvent event) {
        return counters[event.ordinal()].sum();
    }

    private void add(MetricEvent event, long n) {
        counters[event.ordinal()].add(n);
    }

    public void reset() {
        for (MetricEvent event : MetricEvent.values()) {
            counters[event.ordinal()].reset();
        }
    }

    public long exception() {
        return get(MetricEvent.EXCEPTION);
    }

    public long minRt() {
        return minRt;
    }

    public long maxRt() {
        return maxRt;
    }

    public long rt() {
        return get(MetricEvent.RT);
    }

    public void addRt(long rt) {
        add(MetricEvent.RT, rt);
        if (rt < minRt) {
            minRt = rt;
        }
        if (rt > maxRt) {
            maxRt = rt;
        }
    }

    public long success() {
        return get(MetricEvent.SUCCESS);
    }

    public void addException(int n) {
        add(MetricEvent.EXCEPTION, n);
    }

    public void addSuccess(int n) {
        add(MetricEvent.SUCCESS, n);
    }

}
