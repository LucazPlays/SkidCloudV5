package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import org.jline.utils.InfoCmp;

public final class CommandClear extends Command {

    public CommandClear() {
        super("Clear", "skiddet die Konsole.", null, "cls");
    }

    @Override
    public void execute(String[] params) {
        Wrapper.getInstance().getReader().getTerminal().puts(InfoCmp.Capability.clear_screen);
        Wrapper.getInstance().getReader().getTerminal().flush();
        Logger.log("Die Konsole wurde geskiddet!", LogType.INFO);
    }

}
