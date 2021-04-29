package de.terrarier.command.commands;

import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.command.Command;
import de.terrarier.utils.SystemUtil;

public final class CommandSystemInfo extends Command {

    public CommandSystemInfo() {
        super("SystemInfo", "Gibt eine Liste mit Systeminformationen aus!", "", "si");
    }

    @Override
    public void execute(String[] args) {
        Logger.log("OS: " + System.getProperty("os.name"), LogType.INFO);
        Logger.log("OSVersion: " + System.getProperty("os.version"), LogType.INFO);
        Logger.log("GlobalCpuUsage: " + SystemUtil.cpuUsage(), LogType.INFO);
        Logger.log("InternalCpuUsage: " + SystemUtil.internalCpuUsage(), LogType.INFO);
        Logger.log("TotalMemory: " + SystemUtil.getTotalMemory(), LogType.INFO);
        Logger.log("UsedMemory: " + SystemUtil.getUsedMemory(), LogType.INFO);
        Logger.log("FreeMemory: " + SystemUtil.getFreeMemory(), LogType.INFO);
    }

}
