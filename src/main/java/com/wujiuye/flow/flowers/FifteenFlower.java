package com.wujiuye.flow.flowers;

import com.wujiuye.flow.BaseFlower;
import com.wujiuye.flow.common.ArrayMetric;
import com.wujiuye.flow.common.MetricBucket;
import com.wujiuye.flow.common.MetricNode;

import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;


/**
 * 统计一小时及每分钟的流量(这种窗口会有200个数组元素，选择性使用)
 *
 * @author wujiuye
 */
public class FifteenFlower extends BaseFlower {

    public FifteenFlower() {
        /**
         * 最近15分钟的统计信息，将15分钟为每4500毫秒统计一次，样本数为200
         */
        super(new ArrayMetric(200, 15 * 60 * 1000));
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
