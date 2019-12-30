package github.wujiuye.qps;

import java.util.concurrent.atomic.LongAdder;

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
     * 这段事件内的最小耗时
     */
    private volatile long minRt = Integer.MAX_VALUE;

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

    public MetricBucket add(MetricEvent event, long n) {
        counters[event.ordinal()].add(n);
        return this;
    }

    public MetricBucket reset() {
        for (MetricEvent event : MetricEvent.values()) {
            counters[event.ordinal()].reset();
        }
        return this;
    }

    public long exception() {
        return get(MetricEvent.EXCEPTION);
    }

    public long minRt() {
        return minRt;
    }

    public long rt() {
        return get(MetricEvent.RT);
    }

    public void addRT(long rt) {
        add(MetricEvent.RT, rt);
        if (rt < minRt) {
            minRt = rt;
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
