package com.wujiuye.flow;

import com.wujiuye.flow.common.ArrayMetric;
import com.wujiuye.flow.common.MetricNode;

import java.util.Map;


/**
 * 统计一小时及每分钟的流量
 *
 * @author wujiuye
 */
public class HourFlower extends BaseFlower {

    public HourFlower() {
        /**
         * 保存最近60分钟的统计信息。
         * windowLengthInMs设置为60*1000毫秒，这意味着每一个bucket对应每分钟
         */
        super(new ArrayMetric(60, 60 * 60 * 1000));
    }

    @Override
    protected long getWindowInterval(long windowInterval) {
        // 将毫秒转为分钟（平均每分钟）
        return windowInterval / (60 * 1000L);
    }

    @Override
    public Map<Long, MetricNode> lastMetrics() {
        throw new UnsupportedOperationException("不支持，有需要可以自己实现");
    }

}
