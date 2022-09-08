package com.cooldesktop.rmi.server;

import com.cooldesktop.rmi.info.ClassInfo;
import com.cooldesktop.rmi.info.ClassInstanceInfo;
import com.cooldesktop.rmi.info.CoolThreadInfo;
import com.cooldesktop.rmi.info.JvmInfos;
import com.cooldesktop.rmi.server.dump.DumpLoader;
import com.cooldesktop.rmi.server.dump.DumpPrinter;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;

import javax.management.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.rmi.RemoteException;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JvmCallServer implements IJvmCall {
    private static final List<String> FILTER_CLASS = Arrays.asList("org.jd.core.v1", "java", "com.cooldesktop.rmi", "jdk", "[");

    private static void readLine(String source, Function<ClassInstanceInfo, String> hander) {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(source));
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                ClassInstanceInfo classInstanceInfo = parseLine(line);
                if (classInstanceInfo == null) continue;
                hander.apply(classInstanceInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ClassInstanceInfo parseLine(String line) {
        if (line == null || line.equals("") || line.startsWith(" num") || line.startsWith("--------")) return null;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(line);
        List<Integer> integers = new ArrayList<>();
        while (matcher.find()) {
            integers.add(Integer.parseInt(matcher.group()));
        }
        if (integers.size() < 3) return null;
        String name = line.substring(line.lastIndexOf(" ") + 1);
        return new ClassInstanceInfo(name, integers.get(1), integers.get(2));
    }

    @Override
    public List<ClassInstanceInfo> getClassInstanceInfo() throws RemoteException {
        List<ClassInstanceInfo> result = new ArrayList<>();
        try {
            String histogram = (String) ManagementFactory.getPlatformMBeanServer().invoke(
                    new ObjectName("com.sun.management:type=DiagnosticCommand"),
                    "gcClassHistogram",
                    new Object[]{null},
                    new String[]{"[Ljava.lang.String;"});
            readLine(histogram, classInstanceInfo -> {
                result.add(classInstanceInfo);
                return null;
            });
        } catch (InstanceNotFoundException | MBeanException | ReflectionException | MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String hotswap(String className, byte[] clasFile) throws RemoteException {
        Instrumentation instrumentation = JvmInstrumentation.getInstrumentation();
        return new ClassHotswap().hotswap(className, instrumentation, clasFile);
    }

    @Override
    public boolean gc() throws RemoteException {
        System.gc();
        return true;
    }

    @Override
    public String dumpClass(String classLoaderName, String className) throws RemoteException {
        try {
            Instrumentation instrumentation = JvmInstrumentation.getInstrumentation();

            ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
            DumpPrinter dumpPrinter = new DumpPrinter();
            decompiler.decompile(new DumpLoader(className, instrumentation), dumpPrinter, className);
            return dumpPrinter.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    @Override
    public Map<String, List<ClassInfo>> getAllLoadedClass() {
        Instrumentation instrumentation = JvmInstrumentation.getInstrumentation();
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        Map<String, List<ClassInfo>> result = new HashMap<>();
        for (Class item : allLoadedClasses) {
            if (hasFilter(item.getName())) continue;
            try {
                String key = item.getClassLoader() != null ? item.getClassLoader().getClass().toString() : "Boot";
                result.putIfAbsent(key, new ArrayList<>());
                //某版本虚拟机上这里会报错，原因不止，只能这样获取simpleName
                String simpleName = item.getName().substring(item.getName().lastIndexOf(".") + 1);
                result.get(key).add(new ClassInfo(item.getName(), simpleName, key));
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        return result;
    }

    private boolean hasFilter(String name) {
        for (String filterClass : FILTER_CLASS) {
            if (name.startsWith(filterClass)) return true;
        }
        return false;
    }

    private CoolThreadInfo dumpThreadInfo(ThreadInfo threadInfo) {
        CoolThreadInfo coolThreadInfo = new CoolThreadInfo();
        coolThreadInfo.setThreadId(threadInfo.getThreadId());
        coolThreadInfo.setThreadName(threadInfo.getThreadName());
        coolThreadInfo.setBlockedTime(threadInfo.getBlockedTime());
        coolThreadInfo.setBlockedCount(threadInfo.getBlockedCount());
        coolThreadInfo.setWaitedTime(threadInfo.getWaitedTime());
        coolThreadInfo.setWaitedCount(threadInfo.getWaitedCount());
        coolThreadInfo.setLockName(threadInfo.getLockName());
        coolThreadInfo.setLockOwnerId(threadInfo.getLockOwnerId());
        coolThreadInfo.setInNative(threadInfo.isInNative());
        coolThreadInfo.setSuspended(threadInfo.isSuspended());
        coolThreadInfo.setThreadState(threadInfo.getThreadState());

        StackTraceElement[] stackTrace = threadInfo.getStackTrace();
        StringBuffer traceBuffer = new StringBuffer();
        for (StackTraceElement stackTraceElement : stackTrace) {
            traceBuffer.append(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber());
            traceBuffer.append("\r\n");
        }
        coolThreadInfo.setTrace(traceBuffer.toString());
        return coolThreadInfo;
    }

    @Override
    public CoolThreadInfo getThreadInfo(long threadId) {
        ThreadInfo[] threadInfos = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo.getThreadId() == threadId) return dumpThreadInfo(threadInfo);
        }
        return null;

    }

    @Override
    public JvmInfos getJvmInfos() throws RemoteException {
        JvmInfos jvmInfos = new JvmInfos();
        jvmInfos.setStartTime(getStartTime());
        jvmInfos.setUptime(getUpTime());
        jvmInfos.setGcCount(getGcCount());
        jvmInfos.setThreadCount(getThreadCount());
        jvmInfos.setPid(getPid());
        jvmInfos.setHeapSize(getHeapSize());
        jvmInfos.setHeapUsedSize(getHeapUsed());
        jvmInfos.setNoHeapSize(getnoHeapSize());
        jvmInfos.setLoadClassCount(getLoadClassCount());
        jvmInfos.setUnClassCount(getUnClassCount());
        jvmInfos.setThreadIds(getThreadIds());
        jvmInfos.setThreadPeakCount(getPeakThreadCount());
        jvmInfos.setDaemonThreadCount(getDaemonThreadCount());
        jvmInfos.setThreadCountGroupByState(getThreadCountGroupByState());

        jvmInfos.setClassInstanceInfos(getClassInstanceInfo());
        return jvmInfos;
    }

    private Map<String, Integer> getThreadCountGroupByState() {
        Map<String, Integer> result = new HashMap<>();
        long[] allThreadIds = ManagementFactory.getThreadMXBean().getAllThreadIds();
        ThreadInfo[] threadInfo = ManagementFactory.getThreadMXBean().getThreadInfo(allThreadIds);
        for (ThreadInfo info : threadInfo) {
            result.put(info.getThreadState().name(), result.getOrDefault(info.getThreadState().name(), 0) + 1);
        }
        return result;
    }

    private int getDaemonThreadCount() {
        return ManagementFactory.getThreadMXBean().getDaemonThreadCount();
    }

    private int getPeakThreadCount() {
        return ManagementFactory.getThreadMXBean().getPeakThreadCount();
    }

    private Map<String, String> getThreadIds() {
        Map<String, String> result = new HashMap<>();
        ThreadInfo[] threadInfo = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);
        for (ThreadInfo info : threadInfo) {
            result.put(String.valueOf(info.getThreadId()), info.getThreadName());
        }
        return result;
    }

    private long getUnClassCount() {
        return ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount();
    }


    private long getLoadClassCount() {
        return ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
    }

    private long getnoHeapSize() {
        return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax();
    }

    private long getHeapUsed() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    }

    private long getHeapSize() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
    }

    private int getPid() {
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    private int getThreadCount() {
        return ManagementFactory.getThreadMXBean().getThreadCount();
    }

    private long getGcCount() {
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        if (garbageCollectorMXBeans.size() > 0) return garbageCollectorMXBeans.get(0).getCollectionCount();
        return -1;
    }

    private long getUpTime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    private long getStartTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }
}
