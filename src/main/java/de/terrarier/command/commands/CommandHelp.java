package de.terrarier.command.commands;

import de.terrarier.Wrapper;
import de.terrarier.command.Command;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;

public final class CommandHelp extends Command {

    public CommandHelp() {
        super("Help", "Gibt eine Liste mit nützlichen Befehlen aus!", "", "?");
    }

    @Override
    public void execute(String[] params) {
        final Command[] commands = Wrapper.getInstance().getCommands();
        Logger.log("Folgende Befehle sind verfügbar (" + commands.length + "):", LogType.INFO);
        for(Command command : commands) {
            Logger.log(command.getName() + ": " + command.getDescription(), LogType.NONE);
        }
    }

}
