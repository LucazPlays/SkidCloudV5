package de.terrarier.terracloud.command;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.command.commands.*;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.utils.ArrayUtil;

import java.util.concurrent.TimeUnit;

public final class CommandParser {

    private final Command[] commands;

    public CommandParser() {
        commands = new Command[] { new CommandHelp(), new CommandGroup(), new CommandClear(), new CommandReload(),
                new CommandStop(), new CommandShutdown(), new CommandExecute(), new CommandSystemInfo() };
    }

    public void parseCommand(String command) {
        Master.getInstance().getExecutorService().executeDelayed(() -> {
            final String[] args = command.split(" ");
            for (Command cmd : commands) {
                if (matches(cmd, args[0])) {
                    cmd.execute(ArrayUtil.removeFirst(args));
                    return;
                }
            }
            Logger.log("Dieser Befehl existiert nicht, nutze \"help\", um Hilfe zu erhalten!", LogType.WARN);
        }, 25, TimeUnit.MILLISECONDS);
    }

    public Command[] getCommands() {
        return commands;
    }

    private boolean matches(Command command, String raw) {
        if(command.getName().equalsIgnoreCase(raw)) {
            return true;
        }
        for(String alias : command.getAliases()) {
            if(alias.equalsIgnoreCase(raw)) {
                return true;
            }
        }
        return false;
    }

}
