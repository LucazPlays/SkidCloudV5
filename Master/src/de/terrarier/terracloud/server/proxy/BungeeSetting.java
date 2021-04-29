package de.terrarier.terracloud.server.proxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class BungeeSetting {

    private byte[] bungeeFavicon;
    private final String motd;
    private final String motd2;
    private final int slots;

    public BungeeSetting(String motd, String motd2, int slots) {
        this.motd = motd;
        this.motd2 = motd2;
        this.slots = slots;
        final File favicon = new File("./Master/Config/Icon.png");
        if(favicon.exists() && !favicon.isDirectory()) {
            try {
                this.bungeeFavicon = Files.readAllBytes(favicon.toPath());
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.bungeeFavicon = new byte[0];
    }

    public byte[] getBungeeFavicon() {
        return bungeeFavicon;
    }

    public String getMotd() {
        return motd;
    }

    public String getMotd2() {
        return motd2;
    }

    public int getSlots() {
        return slots;
    }

}
