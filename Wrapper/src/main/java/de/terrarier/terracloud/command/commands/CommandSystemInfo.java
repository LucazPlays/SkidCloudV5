package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.utils.SystemUtil;

public final class CommandSystemInfo extends Command {

    public CommandSystemInfo() {
        super("SystemInfo", "Gibt eine Liste mit Systeminformationen aus!", "", "si");
    }

    @Override
    public void execute(String[] args) {
        Logger.log("OS: " + System.getProperty("os.name"), LogType.INFO);
        Logger.log("OSVersion: " + System.getProperty("os.version"), LogType.INFO);
        Logger.log("GlobalCpuUsage: " + (SystemUtil.cpuUsage() * 100), LogType.INFO);
        Logger.log("InternalCpuUsage: " + (SystemUtil.internalCpuUsage() * 100), LogType.INFO);
        Logger.log("TotalMemory: " + SystemUtil.getTotalMemory(), LogType.INFO);
        Logger.log("UsedMemory: " + SystemUtil.getUsedMemory(), LogType.INFO);
        Logger.log("FreeMemory: " + SystemUtil.getFreeMemory(), LogType.INFO);
        Logger.log("  - Skid-Level: " + (Math.random()*100) + "/100", LogType.INFO); //Todo
    }

}
