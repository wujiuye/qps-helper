package com.wujiuye.flow.common;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wujiuye 2021/01/27
 */
public class MetricWriter {

    public static final String METRIC_FILE = "metrics.log";
    public static final String METRIC_FILE_INDEX_SUFFIX = ".idx";
    public static final Comparator<String> METRIC_FILE_NAME_CMP = new MetricFileNameComparator();
    private final static Charset CHARSET = StandardCharsets.UTF_8;

    private String baseDir;
    private String baseFileName;

    private File curMetricFile;
    private File curMetricIndexFile;

    private FileOutputStream outMetric;
    private DataOutputStream outIndex;
    private BufferedOutputStream outMetricBuf;
    private long singleFileSize;
    private int totalFileCount;

    private static long toSeconds(long ms) {
        return ms - (ms % 1000L);
    }

    private long lastSecond = toSeconds(System.currentTimeMillis());

    /**
     * @param baseDir        根目录
     * @param singleFileSize 单个文件的最大大小，如果文件不超过该大小，且一天内服务没有重启过，那么当天的数据只会保存在一个文件中
     * @param totalFileCount 最大保留文件总数，一天内多次重启服务会生成多个文件，totalFileCount并不是指定保留的日记天数
     */
    public MetricWriter(String baseDir, long singleFileSize, int totalFileCount) {
        if (singleFileSize <= 0 || totalFileCount <= 0) {
            throw new IllegalArgumentException();
        }
        this.baseDir = baseDir.replace("//", "/");
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.singleFileSize = singleFileSize;
        this.totalFileCount = totalFileCount;
    }

    public static String formMetricFileName() {
        return METRIC_FILE;
    }

    public static String formIndexFileName(String metricFileName) {
        return metricFileName + METRIC_FILE_INDEX_SUFFIX;
    }

    public synchronized void write(long time, List<MetricNode> entities) throws Exception {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        for (MetricNode entity : entities) {
            entity.setTimestamp(time);
        }
        if (curMetricFile == null) {
            baseFileName = formMetricFileName();
            closeAndNewFile(nextFileNameOfDay(time));
        }
        if (!(curMetricFile.exists() && curMetricIndexFile.exists())) {
            closeAndNewFile(nextFileNameOfDay(time));
        }
        long second = toSeconds(time);
        if (second < lastSecond) {
            // 时间靠前的直接忽略，不应该发生。
        } else if (second == lastSecond) {
            for (MetricNode entity : entities) {
                outMetricBuf.write(entity.toFatString().getBytes(CHARSET));
            }
            outMetricBuf.flush();
            if (!validSize()) {
                closeAndNewFile(nextFileNameOfDay(time));
            }
        } else {
            writeIndex(second, outMetric.getChannel().position());
            if (isNewDay(lastSecond, second)) {
                closeAndNewFile(nextFileNameOfDay(time));
                for (MetricNode entity : entities) {
                    outMetricBuf.write(entity.toFatString().getBytes(CHARSET));
                }
                outMetricBuf.flush();
                if (!validSize()) {
                    closeAndNewFile(nextFileNameOfDay(time));
                }
            } else {
                for (MetricNode entity : entities) {
                    outMetricBuf.write(entity.toFatString().getBytes(CHARSET));
                }
                outMetricBuf.flush();
                if (!validSize()) {
                    closeAndNewFile(nextFileNameOfDay(time));
                }
            }
            lastSecond = second;
        }
    }

    public synchronized void close() throws Exception {
        if (outMetricBuf != null) {
            outMetricBuf.close();
        }
        if (outIndex != null) {
            outIndex.close();
        }
    }

    private void writeIndex(long time, long offset) throws Exception {
        outIndex.writeLong(time);
        outIndex.writeLong(offset);
        outIndex.flush();
    }

    private String nextFileNameOfDay(long time) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(baseDir);
        DateFormat fileNameDf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = fileNameDf.format(new Date(time));
        String fileNameModel = baseFileName + "." + dateStr;
        for (File file : Objects.requireNonNull(baseFile.listFiles())) {
            String fileName = file.getName();
            if (fileName.contains(fileNameModel)
                    && !fileName.endsWith(METRIC_FILE_INDEX_SUFFIX)
                    && !fileName.endsWith(".lck")) {
                list.add(file.getAbsolutePath());
            }
        }
        SortUtil.sort(list, METRIC_FILE_NAME_CMP);
        if (list.isEmpty()) {
            return baseDir + fileNameModel;
        }
        // 获取最后一个文件的序号
        String last = list.get(list.size() - 1);
        int n = 0;
        String[] strs = last.split("\\.");
        if (strs.length > 0 && strs[strs.length - 1].matches("[0-9]{1,10}")) {
            n = Integer.parseInt(strs[strs.length - 1]);
        }
        // 序号加1
        return baseDir + fileNameModel + "." + (n + 1);
    }

    private static final class MetricFileNameComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            // 文件名：xxxx-metrics.log.2018-03-06.01
            String name1 = new File(o1).getName();
            String name2 = new File(o2).getName();
            String dateStr1 = name1.split("\\.")[2];
            String dateStr2 = name2.split("\\.")[2];
            // 比较日期
            int t = dateStr1.compareTo(dateStr2);
            if (t != 0) {
                return t;
            }
            // 日期相同则比较序号
            t = name1.length() - name2.length();
            if (t != 0) {
                return t;
            }
            return name1.compareTo(name2);
        }
    }

    static List<String> listMetricFiles(String baseDir, String baseFileName) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(baseDir);
        File[] files = baseFile.listFiles();
        if (files == null) {
            return list;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()
                    && fileNameMatches(fileName, baseFileName)
                    && !fileName.endsWith(MetricWriter.METRIC_FILE_INDEX_SUFFIX)
                    && !fileName.endsWith(".lck")) {
                list.add(file.getAbsolutePath());
            }
        }
        SortUtil.sort(list, MetricWriter.METRIC_FILE_NAME_CMP);
        return list;
    }

    public static boolean fileNameMatches(String fileName, String baseFileName) {
        if (fileName.contains(baseFileName)) {
            String part = fileName.substring(fileName.indexOf(baseFileName) + baseFileName.length());
            // part is like: ".yyyy-MM-dd.number", eg. ".2018-12-24.11"
            return part.matches("\\.[0-9]{4}-[0-9]{2}-[0-9]{2}(\\.[0-9]*)?");
        } else {
            return false;
        }
    }

    private void removeMoreFiles() {
        List<String> list = listMetricFiles(baseDir, baseFileName);
        if (list.isEmpty()) {
            return;
        }
        for (int i = 0; i < (list.size() - totalFileCount + 1); i++) {
            String fileName = list.get(i);
            new File(fileName).delete();
            String indexFile = formIndexFileName(fileName);
            new File(indexFile).delete();
        }
    }

    private void closeAndNewFile(String fileName) throws Exception {
        removeMoreFiles();
        if (outMetricBuf != null) {
            outMetricBuf.close();
        }
        if (outIndex != null) {
            outIndex.close();
        }
        outMetric = new FileOutputStream(fileName, true);
        outMetricBuf = new BufferedOutputStream(outMetric);
        curMetricFile = new File(fileName);
        String idxFile = formIndexFileName(fileName);
        curMetricIndexFile = new File(idxFile);
        outIndex = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(idxFile, true)));
    }

    private boolean validSize() throws Exception {
        long size = outMetric.getChannel().size();
        return size < singleFileSize;
    }

    private boolean isNewDay(long lastSecond, long second) {
        Date lastDate = new Date(lastSecond);
        Date date = new Date(second);
        DateFormat fileNameDf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDateStr = fileNameDf.format(lastDate);
        String dateStr = fileNameDf.format(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long lastDay = 0;
        long newDay = 0;
        try {
            lastDay = sdf.parse(lastDateStr).getDate();
            newDay = sdf.parse(dateStr).getDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDay > lastDay;
    }

}
