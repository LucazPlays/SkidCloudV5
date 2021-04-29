package de.terrarier.utils;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class SystemUtil {

    public static final Runtime RUNTIME = Runtime.getRuntime();
    private static final int MB = 1024 * 1024;

    public static long getMaxMemory() {
        return RUNTIME.maxMemory() / MB;
    }

    public static long getUsedMemory() {
        return (RUNTIME.totalMemory() - RUNTIME.freeMemory()) / MB;
    }

    public static long getFreeMemory() {
        return RUNTIME.freeMemory() / MB;
    }

    public static long getTotalMemory() {
        return RUNTIME.totalMemory() / MB;
    }

    public static int getAvailableProcessors() {
        return RUNTIME.availableProcessors();
    }

    public static double cpuUsage() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad() * 100;
    }

    public static double internalCpuUsage() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100;
    }

    public static long systemMemory() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
    }

    public static OperatingSystemMXBean system() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
    }

}
