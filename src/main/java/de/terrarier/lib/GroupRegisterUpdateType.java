package de.terrarier.lib;

public enum GroupRegisterUpdateType {

    ADD, REMOVE, UPDATE_NAME, UPDATE_SERVER_COUNT, UPDATE_DYNAMIC, UPDATE_MEMORY;

    private static final GroupRegisterUpdateType[] VALUES = values();

    public static GroupRegisterUpdateType fromId(int id) {
        return VALUES[id];
    }

}
