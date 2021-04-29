package de.terrarier.terracloud.utils;

public class StringUtil {

    public static String combine(String[] toCombine, int offset, String spliterator, int elements) {
        final StringBuilder ret = new StringBuilder();
        for(int i = offset; (i < offset + elements) && i < toCombine.length; i++) {
            if(ret.length() != 0)
                ret.append(spliterator);

            ret.append(toCombine[i]);
        }
        return ret.toString();
    }

    public static String combine(String[] toCombine, int offset, String spliterator) {
        return combine(toCombine, offset, spliterator, toCombine.length - offset);
    }

}
