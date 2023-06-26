package com.wujiuye.flow;

import com.wujiuye.flow.common.MetricBucket;
import com.wujiuye.flow.common.MetricNode;
import com.wujiuye.flow.common.WindowWrap;

import java.util.List;
import java.util.Map;

/**
 * 流量统计
 *
 * @author wujiuye
 */
public interface Flower {

    /**
     * 自增成功数
     *
     * @param rt 耗时
     */
    void incrSuccess(long rt);

    /**
     * 自增异常数
     */
    void incrException();

    /**
     * 总数 = 成功总数 + 异常总数
     *
     * @return
     */
    default long total() {
        return totalSuccess() + totalException();
    }

    /**
     * 当前请求失败率
     *
     * @return
     */
    default double failureRate() {
        long successCount = totalSuccess();
        long exception = totalException();
        return (double) exception / Math.max(1, (successCount + exception));
    }

    /**
     * 成功总数
     *
     * @return
     */
    long totalSuccess();

    /**
     * 均值线
     * 为了节约内存和相当于每个采样的平局值求
     *
     * @return
     */
    long avgRtProp(PropType prop);

    /**
     * 异常总数
     *
     * @return
     */
    long totalException();

    /**
     * 获取异常平均数
     *
     * @return
     */
    long exceptionAvg();

    /**
     * 获取成功平均数
     *
     * @return
     */
    float successAvg();

    /**
     * 平均耗时
     *
     * @return
     */
    long avgRt();

    /**
     * 最小耗时
     *
     * @return
     */
    long minRt();

    /**
     * 最大耗时
     *
     * @return
     */
    long maxRt();

    /**
     * 获取所有bucket
     *
     * @return
     */
    MetricBucket[] buckets();

    /**
     * 获取所有滑动窗口
     *
     * @return
     */
    List<WindowWrap<MetricBucket>> windows();

    /**
     * 获取当前时间的前一秒/一分钟/一小时的指标数据
     * (非线程安全)
     *
     * @return key: 时间戳，value：指标数据快照
     */
    Map<Long, MetricNode> lastMetrics();

}
