package com.wujiuye.flow.flowers;

import com.wujiuye.flow.BaseFlower;
import com.wujiuye.flow.common.ArrayMetric;
import com.wujiuye.flow.common.MetricBucket;
import com.wujiuye.flow.common.MetricNode;

import java.util.Map;
import java.util.PriorityQueue;


/**
 * 统计一小时及每分钟的流量(这种窗口会有500个数组元素，选择性使用)
 *
 * @author wujiuye
 */
public class ThirtyFlower extends BaseFlower {

    public ThirtyFlower() {
        /**
         * 最近30分钟的统计信息，将15分钟为每3600毫秒统计一次，样本数为500
         */
        super(new ArrayMetric(500, 30 * 60 * 1000));
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
