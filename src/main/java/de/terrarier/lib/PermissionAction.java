package de.terrarier.lib;

public enum PermissionAction {

    USER_REMOVE_PERM, USER_ADD_PERM, USER_SET_GROUP, GROUP_DELETE, GROUP_CREATE, GROUP_ADD_PERM, GROUP_REMOVE_PERM;

    public static PermissionAction indexOf(int index) {
        return values()[index];
    }

}
