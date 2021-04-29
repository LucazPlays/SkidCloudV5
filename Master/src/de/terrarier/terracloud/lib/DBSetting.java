package de.terrarier.terracloud.lib;

public final class DBSetting {

    private final String user;
    private final String password;
    private final String host;
    private final int port;
    private final String database;

    public DBSetting(String user, String password, String host, int port, String database) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

}
