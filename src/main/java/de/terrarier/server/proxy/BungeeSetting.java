package de.terrarier.server.proxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class BungeeSetting {

    private byte[] bungeeFavicon;
    private final String motd;
    private final String motd2;

    public BungeeSetting(String motd, String motd2) {
        this.motd = motd;
        this.motd2 = motd2;
        final File favicon = new File("./Wrapper/Files/Icon.png");
        if(favicon.exists() && !favicon.isDirectory()) {
            try {
                this.bungeeFavicon = Files.readAllBytes(favicon.toPath());
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.bungeeFavicon = new byte[2];
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
}
