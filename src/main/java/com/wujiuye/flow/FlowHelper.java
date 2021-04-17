package com.wujiuye.flow;

import java.util.HashMap;
import java.util.Map;

/**
 * 流量统计助手
 */
public class FlowHelper {

    private byte typeFlag;
    private Map<FlowType, Flower> flowMap;

    private Flower newFlow(FlowType flowType) {
        switch (flowType) {
            case Minute:
                return new MinuteFlower();
            case Second:
                return new SecondFlower();
            case Hour:
                return new HourFlower();
            default:
                throw new RuntimeException("not supor type!");
        }
    }

    public FlowHelper(FlowType... types) {
        flowMap = new HashMap<>();
        for (FlowType type : types) {
            typeFlag |= type.getFlag();
            Flower flower = newFlow(type);
            flowMap.put(type, flower);
        }
    }

    /**
     * 每接收一个请求将请求数自增1并添加耗时
     *
     * @param rt 该请求的耗时（毫秒为单位）
     */
    public void incrSuccess(long rt) {
        for (FlowType type : FlowType.values()) {
            if ((typeFlag & type.getFlag()) == type.getFlag()) {
                flowMap.get(type).incrSuccess(rt);
            }
        }
    }

    /**
     * 每出现一次异常，将异常总数自增1
     */
    public void incrException() {
        for (FlowType type : FlowType.values()) {
            if ((typeFlag & type.getFlag()) == type.getFlag()) {
                flowMap.get(type).incrException();
            }
        }
    }

    public Flower getFlow(FlowType flowType) {
        return flowMap.get(flowType);
    }

    public Map<FlowType, Flower> getFlowMap() {
        return new HashMap<>(flowMap);
    }

}
