package com.wujiuye.flow;

import com.wujiuye.flow.common.MetricNode;
import com.wujiuye.flow.common.MetricWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 指标数据持久化
 *
 * @author wujiuye 2021/04/17
 */
public class MetricPersistencer {

    private final static ScheduledThreadPoolExecutor EXECUTOR
            = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

    private final static ConcurrentMap<Flower, Runnable> METRIC_SAVE_TASK = new ConcurrentHashMap<>();
    private final static ConcurrentMap<Flower, MetricWriter> WRITER_MAP = new ConcurrentHashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(EXECUTOR::shutdownNow));
    }

    static String BASE_DIR = System.getProperty("user.home");

    /**
     * 初始化指标数据持久化功能
     *
     * @param baseDir 用于存储指标数据文件的目录
     */
    public static void init(String baseDir) {
        BASE_DIR = baseDir;
        EXECUTOR.scheduleWithFixedDelay(() -> METRIC_SAVE_TASK.values().forEach(Runnable::run),
                0, 1, TimeUnit.SECONDS);
    }

    /**
     * 注册Flower
     *
     * @param name   资源名称
     * @param flower Flower
     */
    public static void registerFlower(String name, Flower flower) {
        WRITER_MAP.put(flower, new MetricWriter(BASE_DIR + "/" + name + "/",
                4096 * 1024, 3));
        METRIC_SAVE_TASK.put(flower, () -> {
            Map<Long, MetricNode> metricNodeMap = flower.lastMetrics();
            Map<Long, List<MetricNode>> map = new HashMap<>();
            for (MetricNode node : metricNodeMap.values()) {
                map.computeIfAbsent(node.getTimestamp(), key -> new ArrayList<>());
                map.get(node.getTimestamp()).add(node);
            }
            // save to file
            map.forEach((key, value) -> {
                try {
                    WRITER_MAP.get(flower).write(key, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * 注销Flower
     *
     * @param flower Flower
     */
    public static void unregisterFlower(Flower flower) {
        METRIC_SAVE_TASK.remove(flower);
        WRITER_MAP.remove(flower);
    }

}
