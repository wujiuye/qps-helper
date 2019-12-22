package github.wujiuye.qps;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

public abstract class LeapArray<T> {

    protected int windowLengthInMs;
    protected int sampleCount;
    protected int intervalInMs;

    protected final AtomicReferenceArray<WindowWrap<T>> array;
    private final ReentrantLock updateLock = new ReentrantLock();

    /**
     * 比如要记录1分钟内每秒的数据则
     * 1、sampleCount样本总数设置60
     * 2、intervalInMs区间毫秒数为 60 * 1000 = 1分钟
     * 则：
     * windowLengthInMs 窗口长度为1000毫秒，即1秒
     * array 的大小为60
     *
     * @param sampleCount
     * @param intervalInMs
     */
    public LeapArray(int sampleCount, int intervalInMs) {
        this.windowLengthInMs = intervalInMs / sampleCount;
        this.intervalInMs = intervalInMs;
        this.sampleCount = sampleCount;
        this.array = new AtomicReferenceArray<>(sampleCount);
    }

    /**
     * 根据当前时间戳获取bucket
     *
     * @return
     */
    public WindowWrap<T> currentWindow() {
        return currentWindow(TimeUtil.currentTimeMillis());
    }

    /**
     * 为bucket创建一个新的统计值
     *
     * @param timeMillis 当前时间戳
     * @return
     */
    public abstract T newEmptyBucket(long timeMillis);

    protected abstract WindowWrap<T> resetWindowTo(WindowWrap<T> windowWrap, long startTime);

    /**
     * 计算索引，将时间戳映射到leap数组。
     *
     * @param timeMillis 时间戳（毫秒）
     * @return
     */
    private int calculateTimeIdx(long timeMillis) {
        /**
         * 假设当前时间戳为1577017699235
         * windowLengthInMs为1000毫秒（1秒）
         * 则
         * 将毫秒转为秒 => 1577017699
         * 映射到数组的索引为 => 19
         *
         */
        long timeId = timeMillis / windowLengthInMs;
        // 取余数起到循环利用的作用
        /**
         * 简单理解，
         * 每1秒统计一次数据，只保存最近一分钟的数据，取余数就是循环利用数组
         * 如果想要连续的一分钟的数据，就不能简单的从头开始遍历数组，而是指定一个开始时间和结束时间，
         * 从开始时间戳开始计算数据存放的数组下标，然后循环每次将开始时间戳加上1秒，直接开始时间等于结束时间。
         * 但由于循环使用的问题，当前时间戳于一分钟之前的时间戳和一分钟之后的时间戳都会映射到同一个下标，
         * 因此必须要能够判断数组下标的数据是否是当前时间的，这便要数组元素存储一个开始时间戳。
         */
        return (int) (timeId % array.length());
    }

    /**
     * 获取bucket开始时间戳
     *
     * @param timeMillis
     * @return
     */
    protected long calculateWindowStart(long timeMillis) {
        /**
         * 假设窗口大小为1000毫秒，即数组每个元素存储1秒钟的统计数据
         * timeMillis % windowLengthInMs 就是取得毫秒部分
         * timeMillis - 毫秒数 = 秒部分
         * 这就得到每秒的开始时间戳
         */
        return timeMillis - timeMillis % windowLengthInMs;
    }

    /**
     * 根据时间戳获取bucket
     *
     * @param timeMillis 时间戳（毫秒）
     * @return 如果时间有效，则在提供的时间戳处显示当前存储桶项；如果时间无效，则为空
     */
    public WindowWrap<T> currentWindow(long timeMillis) {
        if (timeMillis < 0) {
            return null;
        }
        // 获取时间戳映射到的数组索引
        int idx = calculateTimeIdx(timeMillis);
        // 计算bucket的开始时间
        long windowStart = calculateWindowStart(timeMillis);

        // 从数组中获取给定时间的bucket
        while (true) {
            WindowWrap<T> old = array.get(idx);
            // 一搬是项目启动时，时间未到达一个周期，数组还没有存储满，没有到复用阶段，所以数组元素可能为空
            if (old == null) {
                // 创建新的bucket，并创建一个bucket包装器
                WindowWrap<T> window = new WindowWrap<T>(windowLengthInMs, windowStart, newEmptyBucket(timeMillis));
                // cas写入，确保线程安全，期望数组下标的元素是空的，否则就不写入，而是复用了
                if (array.compareAndSet(idx, null, window)) {
                    return window;
                } else {
                    Thread.yield();
                }
            }
            // 如果bucket的开始时间正好是当前时间戳计算出的bucket的开始时间，则就是我们想要的bucket
            else if (windowStart == old.windowStart()) {
                return old;
            }
            // 复用旧的bucket
            else if (windowStart > old.windowStart()) {
                if (updateLock.tryLock()) {
                    try {
                        // 重置bucket，并指定bucket的开始时间为新的开始时间
                        return resetWindowTo(old, windowStart);
                    } finally {
                        updateLock.unlock();
                    }
                } else {
                    Thread.yield();
                }
            }
            // bucket的开始时间比数组当前存储的bucket的开始时间还小，直接返回一个空的bucket就行了
            else if (windowStart < old.windowStart()) {
                return new WindowWrap<T>(windowLengthInMs, windowStart, newEmptyBucket(timeMillis));
            }
        }
    }

    /**
     * Get statistic value from bucket for provided timestamp.
     *
     * @param timeMillis a valid timestamp in milliseconds
     * @return the statistic value if bucket for provided timestamp is up-to-date; otherwise null
     */
    public T getWindowValue(long timeMillis) {
        if (timeMillis < 0) {
            return null;
        }
        int idx = calculateTimeIdx(timeMillis);

        WindowWrap<T> bucket = array.get(idx);

        if (bucket == null || !bucket.isTimeInWindow(timeMillis)) {
            return null;
        }

        return bucket.value();
    }


    public boolean isWindowDeprecated(long time, WindowWrap<T> windowWrap) {
        return time - windowWrap.windowStart() > intervalInMs;
    }

    /**
     * 获取整个滑动窗口的有效存储桶列表。以当前时间为准，往前推算一个周期，
     * 开始时间在该周期内的都算有效
     * 列表将只包含“有效”存储桶。
     *
     * @return
     */
    public List<WindowWrap<T>> list() {
        return list(TimeUtil.currentTimeMillis());
    }

    public List<WindowWrap<T>> list(long validTime) {
        int size = array.length();
        List<WindowWrap<T>> result = new ArrayList<WindowWrap<T>>(size);

        for (int i = 0; i < size; i++) {
            WindowWrap<T> windowWrap = array.get(i);
            if (windowWrap == null || isWindowDeprecated(validTime, windowWrap)) {
                continue;
            }
            result.add(windowWrap);
        }

        return result;
    }

    /**
     * 获取整个滑动窗口的所有存储桶，包括已弃用的存储桶
     *
     * @return
     */
    public List<WindowWrap<T>> listAll() {
        int size = array.length();
        List<WindowWrap<T>> result = new ArrayList<WindowWrap<T>>(size);
        for (int i = 0; i < size; i++) {
            WindowWrap<T> windowWrap = array.get(i);
            if (windowWrap == null) {
                continue;
            }
            result.add(windowWrap);
        }
        return result;
    }

    /**
     * 获取整个滑动窗口的聚合值列表。该列表将只包含“有效”存储桶中的值
     *
     * @return
     */
    public List<T> values() {
        return values(TimeUtil.currentTimeMillis());
    }

    /**
     * 从给定的时间戳开始，只获取一个周期内的元素
     * 结果并不是按开始时间排序的
     *
     * @param timeMillis
     * @return
     */
    public List<T> values(long timeMillis) {
        if (timeMillis < 0) {
            return new ArrayList<T>();
        }
        int size = array.length();
        List<T> result = new ArrayList<T>(size);
        // 从数组0开始遍历，只获取给定时间戳timeMillis往前的一个周期内的记录
        for (int i = 0; i < size; i++) {
            WindowWrap<T> windowWrap = array.get(i);
            if (windowWrap == null || isWindowDeprecated(timeMillis, windowWrap)) {
                continue;
            }
            result.add(windowWrap.value());
        }
        return result;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public int getIntervalInMs() {
        return intervalInMs;
    }

    public double getIntervalInSecond() {
        return intervalInMs / 1000.0;
    }

}
