package de.terrarier.terracloud.server;

import de.terrarier.netlistening.Connection;

public interface ServerInstance<T extends GroupInstance<?>> {

    void playerQuit();

    void playerJoined(int maxPlayers);

    void started(Connection connection);

    void logStarted();

    int getId();

    T getGroup();

    int getPlayers();

    long getStart();

    boolean isStarted();

    int getMaxPlayers();

    boolean isProxy();

}
