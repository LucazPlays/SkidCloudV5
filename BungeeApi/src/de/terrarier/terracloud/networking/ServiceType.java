package de.terrarier.terracloud.networking;

public enum ServiceType {

    PROXY, MINECRAFT, WRAPPER, MASTER;

    public static final ServiceType[] SERVICE_TYPES = values();

    public byte id() {
        return (byte) ordinal();
    }

}
