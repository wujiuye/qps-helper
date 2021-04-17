package com.wujiuye.flow.common;

/**
 * @author wujiuye 2021/04/17
 */
public class MetricNode {

    private long timestamp;
    private long exceptionCnt;
    private long successCnt;
    private long rt;
    private long avgRt;
    private long minRt;
    private long maxRt;

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getExceptionCnt() {
        return exceptionCnt;
    }

    public void setExceptionCnt(long exceptionCnt) {
        this.exceptionCnt = exceptionCnt;
    }

    public long getSuccessCnt() {
        return successCnt;
    }

    public void setSuccessCnt(long successCnt) {
        this.successCnt = successCnt;
    }

    public long getRt() {
        return rt;
    }

    public void setRt(long rt) {
        this.rt = rt;
    }

    public long getAvgRt() {
        return avgRt;
    }

    public void setAvgRt(long avgRt) {
        this.avgRt = avgRt;
    }

    public long getMaxRt() {
        return maxRt;
    }

    public void setMaxRt(long maxRt) {
        this.maxRt = maxRt;
    }

    public long getMinRt() {
        return minRt;
    }

    public void setMinRt(long minRt) {
        this.minRt = minRt;
    }

    public String toFatString() {
        return timestamp + "|" + successCnt + "|" + exceptionCnt + "|" + rt + "|" + minRt + "|" + maxRt + "|" + avgRt + "\n";
    }

}
