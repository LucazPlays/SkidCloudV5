package de.terrarier.command.commands;

import de.terrarier.Wrapper;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.command.Command;

import java.io.IOException;

public final class CommandReload extends Command {

    public CommandReload() {
        super("Reload", "Aktualisiert die Konfigurationen und lädt die neuen Werte aus den Konfigurationsdateien!", "", "rl");
    }

    @Override
    public void execute(String[] params) {
        try {
            Wrapper.getInstance().reload();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("Es ist ein Fehler beim Laden der Konfigurationen aufgetreten!", LogType.CRITICAL);
            return;
        }
        Logger.log("Die Konfigurationen wurden erfolgreich aktualisiert!", LogType.INFO);
    }

}
