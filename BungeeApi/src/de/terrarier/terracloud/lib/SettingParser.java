package de.terrarier.terracloud.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class SettingParser {

    private final Map<String, SettingValue> settings = new HashMap<>();
    private final File file;
    private final String spliterator;

    public SettingParser(File src) {
        this(src, ": ");
    }

    public SettingParser(File src, String spliterator) {
        file = src;
        this.spliterator = spliterator;
        if (src.exists()) {
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(src), 1024);
                String line;
                int index = -1;
                while ((line = reader.readLine()) != null) {
                    index++;
                    if (line.startsWith("#")) {
                        continue;
                    }

                    String[] parts = line.split(spliterator, 2);
                    if (parts.length < 2) {
                        continue;
                    }

                    String value = parts[1];
                    if (value == null) {
                        value = "";
                    }

                    settings.put(parts[0], new SettingValue(value, index));
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                src.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getSetting(String key, String defaultValue) {
        final SettingValue setting = settings.get(key);
        if (setting == null) {
            addLine(key + spliterator + defaultValue);
            return defaultValue;
        }
        return setting.getValue();
    }

    public int getSetting(String key, int defaultValue) {
        final SettingValue setting = settings.get(key);
        if (setting == null) {
            addLine(key + spliterator + defaultValue);
            return defaultValue;
        }
        return Integer.parseInt(setting.getValue());
    }

    public boolean getSetting(String key, boolean defaultValue) {
        final SettingValue setting = settings.get(key);
        if (setting == null) {
            addLine(key + spliterator + defaultValue);
            return defaultValue;
        }
        return Boolean.parseBoolean(setting.getValue());
    }

    public boolean existsSetting(String key) {
        return settings.containsKey(key);
    }

    public int getSettingLineIndex(String key) {
        final SettingValue settingValue = settings.get(key);
        return settingValue != null ? settingValue.getLineIndex() : -1;
    }

    private void addLine(String text) {
        final Path path = file.toPath();
        try {
            final byte[] data = Files.readAllBytes(path);
            final byte[] additional = ((data.length != 0 ? "\r\n" : "") + text).getBytes();
            final byte[] result = new byte[data.length + additional.length];
            System.arraycopy(data, 0, result, 0, data.length);
            System.arraycopy(additional, 0, result, data.length, additional.length);
            Files.write(path, result);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static final class SettingValue {

        private final String value;
        private final int lineIndex;

        public SettingValue(String value, int lineIndex) {
            this.value = value;
            this.lineIndex = lineIndex;
        }

        public String getValue() {
            return value;
        }

        public int getLineIndex() {
            return lineIndex;
        }

    }

}
