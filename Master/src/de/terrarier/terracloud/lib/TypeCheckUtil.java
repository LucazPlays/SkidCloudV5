package de.terrarier.terracloud.lib;

public final class TypeCheckUtil {

    private TypeCheckUtil() {}

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        }catch(NumberFormatException ex) {
            return false;
        }
    }

}
