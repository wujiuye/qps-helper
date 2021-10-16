package com.wujiuye.flow;

import com.wujiuye.flow.common.ArrayMetric;
import com.wujiuye.flow.common.MetricNode;
import com.wujiuye.flow.common.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计一分钟及每秒的流量
 *
 * @author wujiuye
 */
public class MinuteFlower extends BaseFlower {

    /**
     * 上一次收集指标数据快照的时间戳
     */
    private transient long lastFetchTime = -1;

    public MinuteFlower() {
        /**
         * 保存最近60秒的统计信息。
         * windowLengthInMs设置为1000毫秒，这意味着每一个bucket对应每秒
         */
        super(new ArrayMetric(60, 60 * 1000));
    }

    @Override
    protected long getWindowInterval(long windowInterval) {
        // 将毫秒转为秒
        return windowInterval / 1000;
    }

    @Override
    public Map<Long, MetricNode> lastMetrics() {
        long currentTimeMs = TimeUtil.currentTimeMillis();
        // 毫秒部分清零
        currentTimeMs = currentTimeMs - currentTimeMs % 1000;
        Map<Long, MetricNode> metrics = new HashMap<>();
        List<MetricNode> spMetrics = metric.copyCurWindows();
        long newLastFetchTime = lastFetchTime;
        for (MetricNode node : spMetrics) {
            // 取 （lastFetchTime,currentTimeMs）区间的指标数据
            if (node.getTimestamp() > lastFetchTime && node.getTimestamp() < currentTimeMs) {
                metrics.put(node.getTimestamp(), node);
                // 更新lastFetchTime
                newLastFetchTime = Math.max(lastFetchTime, node.getTimestamp());
            }
        }
        lastFetchTime = newLastFetchTime;
        return metrics;
    }

}
