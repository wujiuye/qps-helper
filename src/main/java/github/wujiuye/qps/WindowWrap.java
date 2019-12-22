package github.wujiuye.qps;

/**
 * bucket的包装器实体类
 *
 * @param <T> 存储的数据类型
 * @author wujiuye
 */
public class WindowWrap<T> {

    /**
     * 单个bucket存储桶的时间长度（毫秒）
     */
    private final long windowLengthInMs;
    /**
     * bucket的开始时间戳（毫秒）
     */
    private long windowStart;

    /**
     * 统计数据
     */
    private T value;

    public WindowWrap(long windowLengthInMs, long windowStart, T value) {
        this.windowLengthInMs = windowLengthInMs;
        this.windowStart = windowStart;
        this.value = value;
    }

    public long windowLength() {
        return windowLengthInMs;
    }

    public long windowStart() {
        return windowStart;
    }

    public T value() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    /**
     * 将当前bucket的开始时间戳重置为提供的时间
     *
     * @param startTime 重置后的时间
     * @return
     */
    public WindowWrap<T> resetTo(long startTime) {
        this.windowStart = startTime;
        return this;
    }

    /**
     * 检查给定的时间戳是否在当前bucket中。
     *
     * @param timeMillis 时间戳，毫秒
     * @return
     */
    public boolean isTimeInWindow(long timeMillis) {
        return windowStart <= timeMillis && timeMillis < windowStart + windowLengthInMs;
    }

}
