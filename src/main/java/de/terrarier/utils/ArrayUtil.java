package de.terrarier.utils;

import java.lang.reflect.Array;

public class ArrayUtil {

    public static <T> T[] removeFirst(T[] data) {
        final int dataLength = data.length - 1;
        if (dataLength < 1) // data.length < 2
            return null;

        T[] ret;
        final Class<?> componentType = data.getClass().getComponentType();
        if (componentType.isAssignableFrom(Object.class)) {
            ret = (T[]) Array.newInstance(componentType, dataLength);
        } else if (componentType.isAssignableFrom(String.class)) {
            ret = (T[]) new String[dataLength];
        }else {
            ret = (T[]) Array.newInstance(componentType, dataLength);
        }
        System.arraycopy(data, 1, ret, 0, dataLength);
        return ret;
    }

}
