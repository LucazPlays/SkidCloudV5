package de.terrarier.lib;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public final class PlatformDependentSystemUtil {

    private PlatformDependentSystemUtil() {}

    public static long systemMemory() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
    }

    public static double cpuUsage() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad() * 100;
    }

}
