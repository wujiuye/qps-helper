package com.wujiuye.flow;

import com.wujiuye.flow.common.ArrayMetric;
import com.wujiuye.flow.common.MetricNode;

import java.util.Map;

/**
 * 统计一秒钟的流量
 *
 * @author wujiuye
 */
public class SecondFlower extends BaseFlower {

    public SecondFlower() {
        /**
         * 最近1秒的统计信息，将1000毫秒为每200毫秒统计一次，样本数为5
         */
        super(new ArrayMetric(5, 1000));
    }

    @Override
    protected long getWindowInterval(long windowInterval) {
        return windowInterval / 1000;
    }

    @Override
    public Map<Long, MetricNode> lastMetrics() {
        throw new UnsupportedOperationException("不支持，未来也不会支持");
    }

}
