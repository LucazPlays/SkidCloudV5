package de.terrarier.command.commands;

import de.terrarier.Wrapper;
import de.terrarier.command.Command;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import org.jline.utils.InfoCmp;

public final class CommandClear extends Command {

    public CommandClear() {
        super("Clear", "Leert die Konsole.", null, "cls");
    }

    @Override
    public void execute(String[] params) {
        Wrapper.getInstance().getReader().getTerminal().puts(InfoCmp.Capability.clear_screen);
        Wrapper.getInstance().getReader().getTerminal().flush();
        Logger.log("Die Konsole wurde geleert!", LogType.INFO);
    }

}
