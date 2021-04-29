package de.terrarier.terracloud.utils;

public enum OSType {
	
    MAC_OS("mac", "darwin"),
    WINDOWS("win"),
    LINUX("nux"),
    OTHER("generic");

    private static OSType detectedOS;

    private final String[] keys;

    OSType(String... keys) {
        this.keys = keys;
    }

    private boolean match(String osKey) {
        for (String key : keys) {
            if (osKey.contains(key))
                return true;
        }
        return false;
    }

    public static OSType getOSType() {
        if (detectedOS == null) {
            return detectedOS = getOperatingSystemType(System.getProperty("os.name", OTHER.keys[0]).toLowerCase());
        }
        return detectedOS;
    }

    private static OSType getOperatingSystemType(String osKey) {
        for (OSType osType : values()) {
            if (osType.match(osKey))
                return osType;
        }
        return OTHER;
    }
}
