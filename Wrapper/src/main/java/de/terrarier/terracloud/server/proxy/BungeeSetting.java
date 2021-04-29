package de.terrarier.terracloud.server.proxy;

public final class BungeeSetting {

    private final byte[] favicon;
    private final String motd;
    private final String motd2;
    private final int slots;

    public BungeeSetting(byte[] favicon, String motd, String motd2, int slots) {
        this.favicon = favicon;
        this.motd = motd;
        this.motd2 = motd2;
        this.slots = slots;
    }

    public byte[] getBungeeFavicon() {
        return favicon;
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
