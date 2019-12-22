package github.wujiuye.qps;

public class BucketLeapArray extends LeapArray<MetricBucket> {

    public BucketLeapArray(int sampleCount, int intervalInMs) {
        super(sampleCount, intervalInMs);
    }

    @Override
    public MetricBucket newEmptyBucket(long time) {
        return new MetricBucket();
    }

    /**
     * 更新bucket的开始时间和重置值
     *
     * @param w
     * @param startTime bucket的开始时间（毫秒）
     * @return
     */
    @Override
    protected WindowWrap<MetricBucket> resetWindowTo(WindowWrap<MetricBucket> w, long startTime) {
        w.resetTo(startTime);
        w.value().reset();
        return w;
    }

}
