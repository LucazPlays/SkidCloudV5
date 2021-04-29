package de.terrarier.terracloud;

import de.terrarier.terracloud.lib.DBSetting;
import de.terrarier.terracloud.server.proxy.BungeeSetting;

import java.util.UUID;

public class MasterSetting {

    private final boolean firstStarted;
    private final int masterPort;
    private final int startPort;
    private final int portRange;
    private final DBSetting dbSetting;
    private final BungeeSetting bungeeSetting;
    private final UUID key;

    public MasterSetting(boolean firstStarted, int masterPort, int startPort, int portRange, DBSetting dbSetting, BungeeSetting bungeeSetting, UUID key) {
        this.firstStarted = firstStarted;
        this.masterPort = masterPort;
        this.startPort = startPort;
        this.portRange = portRange;
        this.dbSetting = dbSetting;
        this.bungeeSetting = bungeeSetting;
        this.key = key;
    }

    public boolean isFirstStarted() {
        return this.firstStarted;
    }

    public int getMasterPort() {
        return this.masterPort;
    }

    public int getStartPort() {
        return this.startPort;
    }

    public int getPortRange() {
        return this.portRange;
    }

    public DBSetting getDBSetting() {
        return dbSetting;
    }

    public BungeeSetting getBungeeSetting() {
        return this.bungeeSetting;
    }

    public UUID getKey() {
        return key;
    }

}
