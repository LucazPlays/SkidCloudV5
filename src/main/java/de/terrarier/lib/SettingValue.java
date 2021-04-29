package de.terrarier.lib;

public final class SettingValue {

    private final String value;
    private final int lineIndex;

    public SettingValue(String value, int lineIndex) {
        this.value = value;
        this.lineIndex = lineIndex;
    }

    public String getValue() {
        return this.value;
    }

    public int getLineIndex() {
        return this.lineIndex;
    }

}
