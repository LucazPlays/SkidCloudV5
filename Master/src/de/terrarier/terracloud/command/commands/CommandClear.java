package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import org.jline.utils.InfoCmp;

public final class CommandClear extends Command {

    public CommandClear() {
        super("Clear", "Leert die Konsole.", null, "cls");
    }

    @Override
    public void execute(String[] params) {
        Master.getInstance().getReader().getTerminal().puts(InfoCmp.Capability.clear_screen);
        Master.getInstance().getReader().getTerminal().flush();
        Logger.log("Die Konsole wurde geleert!", LogType.INFO);
    }

}
