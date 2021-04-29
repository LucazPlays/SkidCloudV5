package de.terrarier;

import de.terrarier.file.FileManager;
import de.terrarier.multithreading.CloudScheduledExecutorService;
import de.terrarier.multithreading.CloudThreadFactory;
import de.terrarier.multithreading.executables.InstanceFreezer;
import de.terrarier.multithreading.executables.spigot.StopServer;
import de.terrarier.netlistening.Server;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.netlistening.api.PacketCaching;
import de.terrarier.netlistening.api.encryption.EncryptionSetting;
import de.terrarier.netlistening.api.event.ConnectionDisconnectListener;
import de.terrarier.netlistening.api.event.ConnectionTimeoutListener;
import de.terrarier.netlistening.api.event.DecodeListener;
import de.terrarier.netlistening.impl.ApplicationImpl;
import de.terrarier.command.Command;
import de.terrarier.logging.Color;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.multithreading.executables.proxy.StopProxy;
import de.terrarier.terracloud.network.Sql;
import de.terrarier.terracloud.packet.Packet;
import de.terrarier.terracloud.packet.PacketDirection;
import de.terrarier.terracloud.packet.PacketReload;
import de.terrarier.terracloud.packet.PacketSource;
import de.terrarier.server.proxy.ProxyGroup;
import de.terrarier.server.proxy.ProxyManager;
import de.terrarier.server.proxy.ProxyServer;
import de.terrarier.server.spigot.BukkitServer;
import de.terrarier.server.spigot.ServerGroup;
import de.terrarier.server.spigot.ServerManager;
import de.terrarier.utils.OSType;
import de.terrarier.utils.SystemUtil;
import io.netty.util.ResourceLeakDetector;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Wrapper {
	
	private static Wrapper instance;
	private Sql sql;
	private CloudScheduledExecutorService executorService;
	private WrapperSetting setting;
	private ServerManager serverManager;
	private ProxyManager proxyManager;
	private Server proxyNetworkServer;
	private Server networkServer;
	// private EncryptionInstance encryptionInstance;
	private LineReader reader;
	private Logger logger;
	
	public Wrapper() {
		instance = this;
	}

	/**
	 * Method which starts the Wrapper.
	 *
	 */
	public void start() {
		start(SystemUtil.getAvailableProcessors(), 20350, 20175);
	}

	/**
	 * Method which starts the Wrapper.
	 *
	 * @param threads the number of threads being in the main thread pool
	 * @param proxyNetworkPort the port used for connections with BungeeCord servers
	 * @param serverNetworkPort the port used for connections with Minecraft servers
	 */
	public void start(int threads, int proxyNetworkPort, int serverNetworkPort) {
		AnsiConsole.systemInstall();
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("client.encoding.override", "UTF-8");
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			this.reader = LineReaderBuilder.builder().terminal(TerminalBuilder.builder().system(true).encoding(StandardCharsets.UTF_8).build())
					.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true).build();
			// Logger.useAnsi = true;
			this.logger = new Logger();
		} catch (IOException e) {
			e.printStackTrace();
			Bootstrap.shutdown();
			return;
		}
		Logger.log(Color.YELLOW + "Der Wrapper wird gestartet...", LogType.INFO);
		Logger.log(Color.DARK_RED + " _______                   _____ _                 _ ", LogType.NONE);
		Logger.log(Color.DARK_RED + "|__   __|                 / ____| |               | |", LogType.NONE);
		Logger.log(Color.DARK_RED + "   | | ___ _ __ _ __ __ _| |    | | ___  _   _  __| |", LogType.NONE);
		Logger.log(Color.DARK_RED + "   | |/ _ \\ '__| '__/ _` | |    | |/ _ \\| | | |/ _` |", LogType.NONE);
		Logger.log(Color.DARK_RED + "   | |  __/ |  | | | (_| | |____| | (_) | |_| | (_| |", LogType.NONE);
		Logger.log(Color.DARK_RED + "   |_|\\___|_|  |_|  \\__,_|\\_____|_|\\___/ \\__,_|\\__,_|", LogType.NONE);
		Logger.log(Color.RESET + "", LogType.NONE);
		try {
			this.setting = FileManager.execute(false);
		} catch (IOException e) {
			e.printStackTrace();
			Logger.log("An error occurred while preparing the files!", LogType.ERROR);
			Bootstrap.shutdown();
			return;
		}
		byte[] proxyKeys = loadKeysFromFile(new File("./Wrapper/Files/tmp/ProxyKeys.txt"));
		try {
			this.proxyNetworkServer = new Server.Builder(proxyNetworkPort).compression().varIntCompression(true)
					.nibbleCompression(true).build().caching(PacketCaching.GLOBAL).timeout(15000L)
					.encryption(new EncryptionSetting().init(proxyKeys)).build();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			throw new Error("An internal error occurred while attempting to load the proxy encryption key!");
		}
		if(proxyKeys == null) {
			saveKeysToFile(((ApplicationImpl) this.proxyNetworkServer).getEncryptionSetting().getEncryptionData().keysToByteArray(), new File("./Wrapper/Files/tmp/ProxyKeys.txt"));
		}
		this.proxyNetworkServer.registerListener((DecodeListener) decodeEvent -> Packet._read(decodeEvent.getConnection(), PacketSource.PROXY, decodeEvent.getData()));
		this.proxyNetworkServer.registerListener((ConnectionTimeoutListener) connectionTimeoutEvent -> {
			final ProxyServer server = proxyManager.getServer(connectionTimeoutEvent.getConnection());
			if(server != null) {
				executorService.executeAsync(new StopProxy(server));
			}
		});
		this.proxyNetworkServer.registerListener((ConnectionDisconnectListener) connectionDisconnectEvent -> {
			final ProxyServer server = proxyManager.getServer(connectionDisconnectEvent.getConnection());
			if(server != null) {
				executorService.executeAsync(new StopProxy(server));
			}
		});
		final File mcSrvKeyFile = new File("./Wrapper/Files/tmp/MCServerKeys.txt");
		final byte[] serverKeys = loadKeysFromFile(mcSrvKeyFile);
		try {
			this.networkServer = new Server.Builder(serverNetworkPort).compression().varIntCompression(true)
					.nibbleCompression(true).build().caching(PacketCaching.GLOBAL).timeout(15000)
					.encryption(new EncryptionSetting().init(serverKeys)).build();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			throw new Error("An internal error occurred while attempting to load the server encryption key!");
		}
		if(serverKeys == null) {
			saveKeysToFile(((ApplicationImpl) this.networkServer).getEncryptionSetting().getEncryptionData().keysToByteArray(), mcSrvKeyFile);
		}
		this.networkServer.registerListener((DecodeListener) decodeEvent -> Packet._read(decodeEvent.getConnection(), PacketSource.SERVER, decodeEvent.getData()));
		this.networkServer.registerListener((ConnectionTimeoutListener) connectionTimeoutEvent -> {
			final BukkitServer server = serverManager.getServer(connectionTimeoutEvent.getConnection());
			if(server != null && !server.isFroze() && !server.hasWarmUpBenefit()) {
				executorService.executeAsync(new StopServer(server));
			}
		});
		this.networkServer.registerListener((ConnectionDisconnectListener) connectionDisconnectEvent -> {
			final BukkitServer server = serverManager.getServer(connectionDisconnectEvent.getConnection());
			if(server != null) {
				executorService.executeAsync(new StopServer(server));
			}
		});
		this.sql = new Sql(this.setting.getSqlSetting());
		this.serverManager = new ServerManager();
		this.proxyManager = new ProxyManager();
		this.executorService = new CloudScheduledExecutorService(new CloudThreadFactory("TerraCloud"), threads);
		try {
			this.sql.connect();
		} catch (SQLException throwable) {
			Logger.log("An error occurred while connecting to the sql service - please check the data in the sql file!", LogType.ERROR);
			Bootstrap.shutdown();
			return;
		}
		this.executorService.executeAsync(() -> {
			proxyManager.initGroups();
			serverManager.initGroups();
		});
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			for(ServerGroup group : serverManager.getGroups().values()) {
				for(BukkitServer server : group.getServers()) {
					new StopServer(server, true).run();
				}
			}
			for(ProxyGroup group : proxyManager.getGroups().values()) {
				for(ProxyServer server : group.getServers()) {
					new StopProxy(server, true).run();
				}
			}
			sql.disconnect("Wrapper");
			proxyNetworkServer.stop();
			networkServer.stop();
		}));

		if(OSType.getOSType() == OSType.LINUX) {
			this.executorService.executeRepeating(new InstanceFreezer(), 12000, 12000, TimeUnit.MILLISECONDS);
		}

		Logger.log(Color.DARK_GREEN + "Der Wrapper wurde gestartet!", LogType.INFO);

		this.logger.start();
	}

	/**
	 * @return the instance of the Wrapper.
	 */
	public static Wrapper getInstance() {
		return instance;
	}
	
	public Sql getSql() {
		return this.sql;
	}

	public CloudScheduledExecutorService getExecutorService() {
		return this.executorService;
	}

	public WrapperSetting getSetting() {
		return this.setting;
	}

	public ServerManager getServerManager() {
		return this.serverManager;
	}

	public ProxyManager getProxyManager() {
		return this.proxyManager;
	}

	public void setSetting(WrapperSetting setting) {
		this.setting = setting;
	}

	public void broadcastPacket(Packet packet) {
		DataContainer bungeeData = new DataContainer();
		bungeeData.add(packet.getId());
		packet.write(bungeeData, PacketDirection.PROXY, PacketSource.NONE);
		/*for(ProxyGroup group : this.proxyManager.getGroups()) {
			group.sendData(data);
		}*/
		proxyManager.sendPacket(bungeeData);
		DataContainer serverData = new DataContainer();
		serverData.add(packet.getId());
		packet.write(serverData, PacketDirection.SERVER, PacketSource.NONE);
		serverManager.sendPacket(serverData);
	}

	public void broadcastData(DataContainer data) {
		proxyManager.sendPacket(data);
		serverManager.sendPacket(data);
	}

	public Command[] getCommands() {
		return this.logger.getParser().getCommands();
	}

	public LineReader getReader() {
		return this.reader;
	}

	// TODO: THE CLOUD DELETES ALL TMP SPIGOT SERVERS WHEN THIS GETS EXECUTED - probably fixed!
	public void reload() throws IOException {
		setSetting(FileManager.execute(true));
		broadcastPacket(new PacketReload());
	}

	private static byte[] loadKeysFromFile(File file) {
		if(!file.exists() || file.isDirectory()) {
			return null;
		}
		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void saveKeysToFile(byte[] keys, File file) {
		if(file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			Files.write(file.toPath(), keys);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
