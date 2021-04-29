package de.terrarier.terracloud.lib;

public final class DataCompressionUtil {

    private DataCompressionUtil() {}

    public static byte[] compress(boolean[] data) {
        if(data == null || data.length == 0) {
            return new byte[0];
        }
        return compressUnchecked(data);
    }

    private static int divide(int base, int dividend) {
        if(base % dividend == 0) {
            return base / dividend;
        }
        return (base / dividend) + 1;
    }

    private static boolean fromBit(int bit) {
        return bit == 1;
    }

    public static boolean[] decompress(byte[] data) {
        if(isDataCorrupt(data)) {
            return new boolean[0];
        }
        return decompressUnchecked(data);
    }

    public static boolean isDataCorrupt(byte[] data) {
        return data == null || data.length < 2 || data[0] < 1 || data[0] > 9;
    }

    public static boolean shouldBeCompressed(boolean[] data) {
        return data != null && data.length > 2;
    }

    public static byte[] compressUnchecked(boolean[] data) {
        final int dataLength = data.length;
        final int contentLength = divide(dataLength, 8);
        final byte[] compressed = new byte[contentLength + 1];
        compressed[0] = (byte) (1 + (dataLength % 8));
        for (int i = 0; i < dataLength; i++) {
            if (data[i]) {
                final int div = i / 8/*/ + 1*/;
                final int mod = i % 8;
                if (mod != 0) {
                    compressed[div] = (byte) ((compressed[div] << mod) | 1);
                } else {
                    compressed[div] = (byte) (compressed[div] | 1);
                }
            }
        }
        return compressed;
    }

    public static boolean[] decompressUnchecked(byte[] data) {
        final byte header = (byte) (data[0] - 1);
        final boolean[] decompressed = new boolean[(data.length - 2) * 8 + header];
        for(int i = 0; i < decompressed.length; i++) {
            final int dataIndex = divide(i, 8);
            final int mod = i % 8;
            if(mod != 0) {
                decompressed[i] = fromBit((data[dataIndex] << mod) & 1);
            }else {
                decompressed[i] = fromBit(data[dataIndex] & 1);
            }
        }
        return decompressed;
    }

    // allows max 7 booleans
    public static byte toByte(boolean... values) {
        byte ret = (byte) (values[0] ? 1 : 0);
        for(int i = 1; i < Math.min(7, values.length); i++) {
            if (values[i]) {
                ret = (byte) ((ret << i) | 1);
            }
        }
        return ret;
    }

    public static boolean[] fromByte(byte compressed, int values) {
        if(values == 0)
            return new boolean[0];

        final boolean[] ret = new boolean[Math.min(values, 7)];
        ret[0] = fromBit(compressed & 1);
        for(int i = 1; i < ret.length; i++) {
            ret[i] = fromBit((compressed << i) & 1);
        }
        return ret;
    }

}
