package com.cooldesktop.rmi.info;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class JvmInfos implements Serializable {

    /**
    * @description: 启动时间
    * @date: 2022/8/21 下午4:20
    */
    private long startTime;


    /**
    * @description: 进程id
    * @date: 2022/8/21 下午4:20
    */
    private int pid;

    /**
    * @description: 已运行
    * @date: 2022/8/21 下午4:20
    */
    private long uptime;


    /**
    * @description: 线程数量
    * @date: 2022/8/21 下午4:21
    */
    private int threadCount;


    /**
    * @description: GC次数
    * @date: 2022/8/21 下午4:21
    */
    private long gcCount;


    /**
    * @description: 堆大小
    * @date: 2022/8/21 上午10:20
    */

    private long heapSize;


    /**
     * @description: 非堆大小
     * @date: 2022/8/21 上午10:20
     */
    private long noHeapSize;

    /**
    * @description: 堆已经使用大小
    * @date: 2022/8/21 上午10:21
    */

    private long heapUsedSize;

    /**
     * @description: 类加载数量
     * @date: 2022/8/21 上午10:24
     */

    private long loadClassCount;


    /**
    * @description: 类卸载数量
    * @date: 2022/8/21 上午10:26
    */

    private long unClassCount;


    /**
    * @description: 线程id
    * @date: 2022/8/21 下午1:53
    */

    private Map<String,String> threadIds;


    /**
    * @description: 线程分组数量
    * @date: 2022/8/23 上午9:03
    */

    private Map<String,Integer> threadCountGroupByState;


    /**
    * @description: 线程峰值
    * @date: 2022/8/23 上午8:24
    */

    private int  threadPeakCount;


    /**
    * @description: 守护线程
    * @date: 2022/8/24 上午9:41
    */

    private int daemonThreadCount;
    
    
    /**
    * @description: 类实例数量
    * @date: 2022/8/24 上午10:20
    */
    
    private List<ClassInstanceInfo> classInstanceInfos;

    public List<ClassInstanceInfo> getClassInstanceInfos() {
        return classInstanceInfos;
    }

    public void setClassInstanceInfos(List<ClassInstanceInfo> classInstanceInfos) {
        this.classInstanceInfos = classInstanceInfos;
    }

    public int getDaemonThreadCount() {
        return daemonThreadCount;
    }

    public void setDaemonThreadCount(int daemonThreadCount) {
        this.daemonThreadCount = daemonThreadCount;
    }

    public int getThreadPeakCount() {
        return threadPeakCount;
    }

    public void setThreadPeakCount(int threadPeakCount) {
        this.threadPeakCount = threadPeakCount;
    }

    public Map<String, Integer> getThreadCountGroupByState() {
        return threadCountGroupByState;
    }

    public void setThreadCountGroupByState(Map<String, Integer> threadCountGroupByState) {
        this.threadCountGroupByState = threadCountGroupByState;
    }

    public Map<String, String> getThreadIds() {
        return threadIds;
    }

    public void setThreadIds(Map<String, String> threadIds) {
        this.threadIds = threadIds;
    }

    public long getUnClassCount() {
        return unClassCount;
    }

    public void setUnClassCount(long unClassCount) {
        this.unClassCount = unClassCount;
    }

    public long getLoadClassCount() {
        return loadClassCount;
    }

    public void setLoadClassCount(long loadClassCount) {
        this.loadClassCount = loadClassCount;
    }



    public long getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(long heapSize) {
        this.heapSize = heapSize;
    }

    public long getNoHeapSize() {
        return noHeapSize;
    }

    public void setNoHeapSize(long noHeapSize) {
        this.noHeapSize = noHeapSize;
    }

    public long getHeapUsedSize() {
        return heapUsedSize;
    }

    public void setHeapUsedSize(long heapUsedSize) {
        this.heapUsedSize = heapUsedSize;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public long getGcCount() {
        return gcCount;
    }

    public void setGcCount(long gcCount) {
        this.gcCount = gcCount;
    }
}
