package com.wujiuye.flow;

import com.wujiuye.flow.common.ArrayMetric;

/**
 * 统计一分钟及每秒的流量
 *
 * @author wujiuye
 */
public class MinuteFlower extends BaseFlower {

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

}
