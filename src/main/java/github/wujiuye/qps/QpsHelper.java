package github.wujiuye.qps;

/**
 * qps统计助手
 * 不会持久化数据
 */
public class QpsHelper {

    /**
     * 最近1秒的统计信息，将1000毫秒为每200毫秒统计一次，样本数为5
     */
    private transient volatile Metric rollingCounterInSecond = new ArrayMetric(5, 1000);

    /**
     * 保存最近60秒的统计信息。
     * windowLengthInMs设置为1000毫秒，这意味着每一个bucket对应每秒
     */
    private transient Metric rollingCounterInMinute = new ArrayMetric(60, 60 * 1000);

    /**
     * 每接收一个请求将请求数自增1并添加耗时
     *
     * @param rt 该请求的耗时（毫秒为单位）
     */
    public void incrSuccess(long rt) {
        rollingCounterInSecond.addSuccess(1);
        rollingCounterInSecond.addRT(rt);
        rollingCounterInMinute.addSuccess(1);
        rollingCounterInMinute.addRT(rt);
    }

    /**
     * 每出现一次异常，将异常总数自增1
     */
    public void incrException() {
        rollingCounterInMinute.addException(1);
        rollingCounterInSecond.addException(1);
    }

    // ====================  分钟为单位的统计 ===============================

    public long totalRequestInMinute() {
        return totalSuccessInMinute() + totalExceptionInMinute();
    }

    public long totalSuccessInMinute() {
        return rollingCounterInMinute.success();
    }

    public long totalExceptionInMinute() {
        return rollingCounterInMinute.exception();
    }

    /**
     * 最小耗时
     *
     * @return
     */
    public double minRtInMinute() {
        return rollingCounterInMinute.minRt();
    }

    /**
     * 成功请求数的平均耗时
     *
     * @return
     */
    public double avgRtInMinute() {
        long successCount = rollingCounterInMinute.success();
        if (successCount == 0) {
            return 0;
        }
        return rollingCounterInMinute.rt() * 1.0 / successCount;
    }

    /**
     * 异常的平均qps
     *
     * @return
     */
    public double exceptionAvgQps() {
        return rollingCounterInMinute.exception() / rollingCounterInMinute.getWindowIntervalInSec();
    }

    /**
     * 成功的平均qps
     *
     * @return
     */
    public double successAvgQps() {
        return rollingCounterInMinute.success() / rollingCounterInMinute.getWindowIntervalInSec();
    }

    // ===================  秒为单位的统计 ==============================

    public long totalRequestInSec() {
        return totalSuccessInSec() + totalExceptionInMinute();
    }

    public long totalSuccessInSec() {
        return rollingCounterInSecond.success();
    }

    public long totalExceptionInSec() {
        return rollingCounterInSecond.exception();
    }

    /**
     * 最小耗时
     *
     * @return
     */
    public double minRtInSec() {
        return rollingCounterInSecond.minRt();
    }

    /**
     * 成功请求数的平均耗时
     *
     * @return
     */
    public double avgRtInSec() {
        long successCount = rollingCounterInSecond.success();
        if (successCount == 0) {
            return 0;
        }
        return rollingCounterInSecond.rt() * 1.0 / successCount;
    }

    /**
     * 异常的qps
     *
     * @return
     */
    public double exceptionQps() {
        return rollingCounterInSecond.exception() / rollingCounterInSecond.getWindowIntervalInSec();
    }

    /**
     * 成功的qps
     *
     * @return
     */
    public double successQps() {
        return rollingCounterInSecond.success() / rollingCounterInSecond.getWindowIntervalInSec();
    }

}
