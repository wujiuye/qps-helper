package com.wujiuye.flow;

public enum FlowType {

    /**
     * 秒
     */
    Second((byte) 0b00000001),
    /**
     * 分
     */
    Minute((byte) 0b00000010),

    /**
     * 小时
     */
    Hour((byte) 0b00000100),

    /**
     * 五分钟
     */
    FiveMinute((byte) 0b00001000),

    /**
     * 十五分钟
     */
    FifteenMinute((byte) 0b00010000),

    /**
     * 三十分钟
     */
    ThirtyMinute((byte) 0b00100000);


    byte flag;

    FlowType(byte flag) {
        this.flag = flag;
    }

    public byte getFlag() {
        return flag;
    }

}
