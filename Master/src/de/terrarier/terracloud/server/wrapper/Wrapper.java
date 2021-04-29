package de.terrarier.terracloud.server.wrapper;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.networking.Packet;
import de.terrarier.terracloud.networking.ServiceType;

public final class Wrapper {

    private final String name;
    private final int cores;
    private final Connection connection;
    private double cpuLoad;
    private long freeMemory;

    public Wrapper(String name, int cores, Connection connection, long freeMemory) {
        this.name = name;
        this.cores = cores;
        this.connection = connection;
        this.freeMemory = freeMemory;
    }

    public String getName() {
        return name;
    }

    public int getCores() {
        return cores;
    }

    public Connection getConnection() {
        return connection;
    }

    public void updateLoad(double cpuLoad, long freeMemory) {
        this.cpuLoad = cpuLoad;
        this.freeMemory = freeMemory;
    }

    public void reduceMemory(long reduction) {
        freeMemory -= reduction;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void sendPacket(Packet packet) {
        final DataContainer dataContainer = new DataContainer();
        packet.write(dataContainer, ServiceType.WRAPPER);
        connection.sendData(dataContainer);
    }

    public void sendData(DataContainer dataContainer) {
        connection.sendData(dataContainer);
    }

}
