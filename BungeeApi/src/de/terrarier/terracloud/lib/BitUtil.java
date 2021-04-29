package de.terrarier.terracloud.lib;

public final class BitUtil {

    private BitUtil() {}

    public static int getBit(byte base, int index) {
        if(index == 0) {
            return base & 1;
        }
        return (base >> index) & 1;
    }

    public static byte modifyBit(byte base, int index, int value) {
        if (getBit(base, index) == value) {
            return base;
        }
        return (byte) (base ^ (1 << index));
    }

    public static boolean isBitSet(byte base, int index) {
        return getBit(base, index) == 1;
    }

}
