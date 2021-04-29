package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;

public final class CommandStop extends Command {

    public CommandStop() {
        super("Stop", "Stoppt den Wrapper!", "", "end");
    }

    @Override
    public void execute(String[] params) {
        Logger.log("Der Wrapper wird nun heruntergefahren...", LogType.INFO);
        System.exit(0);
    }

}
