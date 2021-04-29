package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;

public final class CommandHelp extends Command {

    public CommandHelp() {
        super("Help", "Gibt eine Liste mit nützlichen Befehlen aus!", "", "?");
    }

    @Override
    public void execute(String[] params) {
        final Command[] commands = Master.getInstance().getCommands();
        Logger.log("Folgende Befehle sind verfügbar (" + commands.length + "):", LogType.INFO);
        for(Command command : commands) {
            Logger.log(command.getName() + ": " + command.getDescription(), LogType.NONE);
        }
    }

}
