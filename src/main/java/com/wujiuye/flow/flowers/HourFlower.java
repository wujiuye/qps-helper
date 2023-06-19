package com.wujiuye.flow.flowers;

import com.wujiuye.flow.BaseFlower;
import com.wujiuye.flow.common.ArrayMetric;
import com.wujiuye.flow.common.MetricBucket;
import com.wujiuye.flow.common.MetricNode;

import java.util.Arrays;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * 统计一小时及每分钟的流量
 *
 * @author wujiuye
 */
public class HourFlower extends BaseFlower {

    public HourFlower() {
        /**
         * 最近1小时的统计信息，将1小时为每1分钟统计一次，样本数为60
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
