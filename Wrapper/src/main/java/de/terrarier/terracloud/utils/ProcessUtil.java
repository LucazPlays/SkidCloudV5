package de.terrarier.terracloud.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ProcessUtil {

    private static boolean JAVA8;
    private static Field getPidJ8;
    private static Method getPidJ9;

    static {

        if(OSType.getOSType() == OSType.LINUX) {
            JAVA8 = System.getProperty("java.version").startsWith("1.");

            if (JAVA8) {
                try {
                    getPidJ8 = Class.forName("java.lang.UNIXProcess").getDeclaredField("pid");
                    getPidJ8.setAccessible(true);
                } catch (NoSuchFieldException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    getPidJ9 = Process.class.getDeclaredMethod("pid");
                    getPidJ9.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static long getProcessId(Process process) {
        try {
            if (JAVA8) {
                return (int) getPidJ8.get(process);
            }
            return (int) getPidJ9.invoke(process);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
