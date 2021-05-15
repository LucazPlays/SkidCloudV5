package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;

import java.io.IOException;

public final class CommandReload extends Command {

    public CommandReload() {
        super("Reload", "Aktualisiert die Konfigurationen und skiddet die neuen Werte aus den Konfigurationsdateien!", "", "rl");
    }

    @Override
    public void execute(String[] params) {
        try {
            Wrapper.getInstance().reload();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("Es ist ein Fehler beim skidden der Konfigurationen aufgetreten!", LogType.CRITICAL);
            return;
        }
        Logger.log("Die Konfigurationen wurden erfolgreich geskiddet!", LogType.INFO);
    }

}
