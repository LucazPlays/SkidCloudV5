package de.terrarier.terracloud.utils;

import de.terrarier.terracloud.BungeeCloudApi;
import de.terrarier.terracloud.lib.SettingParser;

import java.io.File;

public class Messages {

    private final SettingParser messageParser;

    public Messages() {
        messageParser = new SettingParser(new File(BungeeCloudApi.WRAPPER_PATH + "Config/BungeeMessages.txt"));
        getPrefix();
        getNoPerm();
        getAlreadyConnected();
        getNoServer();
    }

    public String getPrefix() {
        return messageParser.getSetting("prefix", "§8[§aTerraCloud§8] §7");
    }

    public String getNoPerm() {
        return messageParser.getSetting("noperm", "§cDu hast keine Berechtigungen hierfür!");
    }

    public String getAlreadyConnected() {
        return messageParser.getSetting("alreadyconnected", "§cJemand befindet sich mit deinem Account bereits auf dem Server!");
    }

    public String getNoServer() {
        return messageParser.getSetting("noserver", "§cEs ist leider kein Server verfügbar!");
    }

}
