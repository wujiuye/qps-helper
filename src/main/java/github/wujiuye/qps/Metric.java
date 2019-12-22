package github.wujiuye.qps;

/**
 * 度量接口
 *
 * @author wujiuyu
 */
public interface Metric {

    /**
     * 获取成功总数
     */
    long success();

    /**
     * 异常总数
     */
    long exception();

    /**
     * 总的响应时间
     */
    long rt();

    /**
     * 最小响应时间
     */
    long minRt();

    /**
     * 获取bucket数组
     */
    MetricBucket[] buckets();

    /**
     * 添加异常数
     */
    void addException(int n);

    /**
     * 添加成功数
     *
     * @param n count to add
     */
    void addSuccess(int n);

    /**
     * 添加一个请求的总耗时
     *
     * @param rt RT
     */
    void addRT(long rt);

    /**
     * 获取以秒为单位的滑动窗口长度
     */
    double getWindowIntervalInSec();

    /**
     * 样本总数，统计的bucket数，比如统计1分钟的每秒qps，那么样本数就是60
     *
     * @return
     */
    int getSampleCount();

}
