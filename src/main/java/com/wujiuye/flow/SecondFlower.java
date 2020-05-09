package com.wujiuye.flow;

import com.wujiuye.flow.common.ArrayMetric;

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

}
