package de.terrarier.lib;

public final class SqlSetting {

    private final String host;
    private final String user;
    private final String password;
    private final String database;
    private final int port;

    public SqlSetting(String host, String user, String password, String database, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    public String getHost() {
        return this.host;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDatabase() {
        return this.database;
    }

    public int getPort() {
        return this.port;
    }
}
