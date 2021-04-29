package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketReload;

public final class CommandReload extends Command {

    public CommandReload() {
        super("Reload", "Aktualisiert die Konfigurationen und lädt die neuen Werte aus den Konfigurationsdateien!", "", "rl");
    }

    @Override
    public void execute(String[] params) {
        Master.getInstance().reload();
        Master.getInstance().broadcastPacket(new PacketReload(), null);
        Logger.log("Die Konfigurationen wurden erfolgreich aktualisiert!", LogType.INFO);
    }

}
