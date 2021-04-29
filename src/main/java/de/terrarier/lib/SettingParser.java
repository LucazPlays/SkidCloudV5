package de.terrarier.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SettingParser {
	
	private final Map<String, SettingValue> settings = new HashMap<>();
	
	public SettingParser(File src) {
		this(src, ": ");
	}

	public SettingParser(File src, String spliterator) {
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(src), 1024);
			String line;
			int index = -1;
			while((line = reader.readLine()) != null) {
				index++;
				if(line.startsWith("#"))
					continue;

				final String[] parts = line.split(spliterator, 2);
				if(parts.length < 2)
					continue;

				final String value = parts[1];
				if(value == null || value.length() == 0)
					continue;

				this.settings.put(parts[0], new SettingValue(value, index));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getSetting(String key, String defaultValue) {
		final SettingValue setting = settings.get(key);
		return setting != null ? setting.getValue() : defaultValue;
	}
	
	public boolean existsSetting(String key) {
		return this.settings.containsKey(key);
	}

	public int getSettingLineIndex(String key) {
		final SettingValue settingValue = settings.get(key);
		return settingValue != null ? settingValue.getLineIndex() : -1;
	}

}
