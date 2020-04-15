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
    Hour((byte) 0b00000100);

    byte flag;

    FlowType(byte flag) {
        this.flag = flag;
    }

    public byte getFlag() {
        return flag;
    }

}
