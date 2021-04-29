package de.terrarier.terracloud;

import de.terrarier.netlistening.Client;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.netlistening.api.encryption.ServerKey;
import de.terrarier.netlistening.api.event.ConnectionTimeoutListener;
import de.terrarier.netlistening.api.event.DecodeListener;
import de.terrarier.netlistening.api.event.KeyChangeEvent;
import de.terrarier.netlistening.api.event.KeyChangeListener;
import de.terrarier.terracloud.lib.CustomPayloadUtil;
import de.terrarier.terracloud.listeners.Registry;
import de.terrarier.terracloud.networking.Packet;
import de.terrarier.terracloud.networking.PacketRegister;
import de.terrarier.terracloud.server.proxy.ProxyManager;
import de.terrarier.terracloud.server.spigot.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class CloudApi extends JavaPlugin {

    public static final String SERVER_GROUP;
    public static final int SERVER_ID;
    public static final String SERVER_NAME;
    private static final String INTEGER_PATTERN = "[0-9]";
    private static final String PATH;
    private static final String WRAPPER_PATH;
    private static CloudApi instance;
    private final ServerManager serverManager = new ServerManager();
    private final ProxyManager proxyManager = new ProxyManager();
    private final Client client;

    static {
        final String tmpPath = new File("tmp").getAbsolutePath();
        PATH = tmpPath.substring(0, tmpPath.length() - 3 - 1);
        String[] parts = PATH.split("/Wrapper/");
        if (parts.length < 2) {
            parts = PATH.split("\\\\Wrapper\\\\");
        }

        WRAPPER_PATH = parts[parts.length - 2] + "/Wrapper/";

        String[] pathParts = PATH.split("/");
        if(pathParts.length < 2) {
            pathParts = PATH.split("\\\\");
        }
        SERVER_NAME = pathParts[pathParts.length - 1];
        final String[] nameParts = SERVER_NAME.split("-");
        final String toParse = Pattern.matches(INTEGER_PATTERN, nameParts[1]) ? nameParts[1] : nameParts[1].substring(0, 1);
        SERVER_ID = Integer.parseInt(toParse);
        SERVER_GROUP = nameParts[0];
    }

    public CloudApi() {
        instance = this;
        client = Client.builder("localhost",
                Integer.parseInt(System.getProperty("netport", "20175"))).timeout(15000L).build();
        client.registerListener((DecodeListener) decodeEvent -> Packet.read(decodeEvent.getData()));
        client.registerListener((ConnectionTimeoutListener) connection -> Bukkit.shutdown());
        final File keyHashFile = new File(WRAPPER_PATH + "/Files/tmp/MCServerKeyHash.txt");
        if (!keyHashFile.exists()) {
            client.registerListener((KeyChangeListener) keyChangeEvent -> {
                if (keyChangeEvent.getResult() == KeyChangeEvent.KeyChangeResult.HASH_ABSENT) {
                    if (!keyHashFile.exists()) {
                        try {
                            keyHashFile.createNewFile();
                            Files.write(keyHashFile.toPath(), ServerKey.fromHash(keyChangeEvent.getReceivedKeyHash(), client).toByteArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            try {
                client.setServerKey(Files.readAllBytes(keyHashFile.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            client.registerListener((KeyChangeListener) keyChangeEvent -> {
                if (keyChangeEvent.getResult() == KeyChangeEvent.KeyChangeResult.HASH_CHANGED) {
                    keyChangeEvent.setCancelled(true);
                    try {
                        throw new IllegalStateException("Detected potential MITM attack - shutting down!");
                    } finally {
                        Bukkit.shutdown();
                    }
                }
            });
        }
        Runtime.getRuntime().addShutdownHook(new Thread(client::disconnect));
        CustomPayloadUtil.init(client);
    }

    @Override
    public void onEnable() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new Registry(), this);
        sendPacket(new PacketRegister());
    }

    @Override
    public void onDisable() {
        client.stop();
    }

    public void sendPacket(Packet packet) {
        final DataContainer dataContainer = new DataContainer();
        packet.write(dataContainer);
        client.sendData(dataContainer);
    }

    public static CloudApi getInstance() {
        return instance;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public ProxyManager getProxyManager() {
        return proxyManager;
    }

}
