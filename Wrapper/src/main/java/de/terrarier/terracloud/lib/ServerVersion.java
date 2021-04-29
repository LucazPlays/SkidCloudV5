package de.terrarier.terracloud.lib;

public enum ServerVersion {

    V1_8_8, V1_9_4, V1_10_2, V1_11_2, V1_12_2, V1_13_2, V1_14_4, V1_15_2, V1_16_5;

    private final String cleanedName;

    ServerVersion() {
        this.cleanedName = name().replaceFirst("V", "").replace('_', '.');
    }

    public String getCleanedName() {
        return cleanedName;
    }
}
