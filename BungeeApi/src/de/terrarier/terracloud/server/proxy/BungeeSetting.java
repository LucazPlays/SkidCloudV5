package de.terrarier.terracloud.server.proxy;

import net.md_5.bungee.api.Favicon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class BungeeSetting {

    private final Favicon favicon;
    private final String motd;
    private final String motd2;
    private final int slots;

    public BungeeSetting(byte[] favicon, String motd, String motd2, int slots) {
        final BufferedImage image = createImageFromBytes(favicon);
        this.favicon = image == null ? null : Favicon.create(image);
        this.motd = motd;
        this.motd2 = motd2;
        this.slots = slots;
    }

    private static BufferedImage createImageFromBytes(byte[] imageData) {
        if(imageData.length == 0) {
            return null;
        }
        try (ByteArrayInputStream input = new ByteArrayInputStream(imageData)) {
            return ImageIO.read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Favicon getFavicon() {
        return favicon;
    }

    public boolean existsFavicon() {
        return favicon != null;
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
