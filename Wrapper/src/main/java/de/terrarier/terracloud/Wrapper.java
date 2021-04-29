package de.terrarier.terracloud;

import de.terrarier.netlistening.Client;
import de.terrarier.netlistening.Server;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.netlistening.api.PacketCaching;
import de.terrarier.netlistening.api.encryption.EncryptionSetting;
import de.terrarier.netlistening.api.event.ConnectionDisconnectListener;
import de.terrarier.netlistening.api.event.ConnectionPostInitListener;
import de.terrarier.netlistening.api.event.ConnectionTimeoutListener;
import de.terrarier.netlistening.api.event.DecodeListener;
import de.terrarier.netlistening.impl.ApplicationImpl;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.file.FileManager;
import de.terrarier.terracloud.logging.Color;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.multithreading.CloudScheduledExecutorService;
import de.terrarier.terracloud.multithreading.CloudThreadFactory;
import de.terrarier.terracloud.multithreading.executables.InstanceFreezer;
import de.terrarier.terracloud.multithreading.executables.proxy.StopProxy;
import de.terrarier.terracloud.multithreading.executables.spigot.StopServer;
import de.terrarier.terracloud.networking.*;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyManager;
import de.terrarier.terracloud.server.proxy.ProxyServer;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.LocalBukkitServerImpl;
import de.terrarier.terracloud.server.spigot.ServerGroup;
import de.terrarier.terracloud.server.spigot.ServerManager;
import de.terrarier.terracloud.utils.OSType;
import de.terrarier.terracloud.utils.ShutdownController;
import de.terrarier.terracloud.utils.SystemUtil;
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
import java.util.concurrent.TimeUnit;

public class Wrapper {
	
	private static Wrapper instance;
	private CloudScheduledExecutorService executorService;
	private WrapperSetting setting;
	private final ServerManager serverManager = new ServerManager();
	private final ProxyManager proxyManager = new ProxyManager();
	private Server proxyNetworkServer;
	private Server networkServer;
	private LineReader reader;
	private Logger logger;
	private Client masterClient;
	private final FileManager fileManager = new FileManager();
	private final ShutdownController shutdownController = new ShutdownController(6,
			SystemUtil.getAvailableProcessors() * 8, 15 * 1000L);
	private boolean shutDown;
	
	public Wrapper() {
		instance = this;
	}

	/**
	 * Starts the Wrapper.
	 *
	 */
	public void start() {
		start(SystemUtil.getAvailableProcessors(), Integer.parseInt(System.getProperty("proxynetport", "20350")), Integer.parseInt(System.getProperty("netport", "20175")));
	}

	/**
	 * Starts the Wrapper.
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
			this.reader = LineReaderBuilder.builder().terminal(TerminalBuilder.builder().system(true).dumb(true).encoding(StandardCharsets.UTF_8).build())
					.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true).build();
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
			this.setting = FileManager.execute(false, fileManager);
		} catch (IOException e) {
			e.printStackTrace();
			Logger.log("An error occurred while preparing the files!", LogType.ERROR);
			Bootstrap.shutdown();
			return;
		}
		executorService = new CloudScheduledExecutorService(new CloudThreadFactory("TerraCloud"), threads);
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
		this.proxyNetworkServer.registerListener((DecodeListener) decodeEvent -> Packet.read(decodeEvent.getData(), decodeEvent.getConnection()));
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
		this.networkServer.registerListener((DecodeListener) decodeEvent -> Packet.read(decodeEvent.getData(), decodeEvent.getConnection()));
		this.networkServer.registerListener((ConnectionTimeoutListener) connectionTimeoutEvent -> {
			final LocalBukkitServerImpl server = (LocalBukkitServerImpl) serverManager.getServer(connectionTimeoutEvent.getConnection());
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
		masterClient = Client.builder(setting.getMasterHost(), setting.getMasterPort()).timeout(15000).build();
		masterClient.registerListener((ConnectionPostInitListener) connectionPostInitEvent ->
				sendToMaster(new PacketRegister(setting.getName(), ServiceType.WRAPPER)));
		masterClient.registerListener((DecodeListener) decodeEvent -> Packet.read(decodeEvent.getData(),
				decodeEvent.getConnection()));
		this.executorService = new CloudScheduledExecutorService(new CloudThreadFactory("TerraCloud"), threads);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			shutDown = true;
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
			proxyNetworkServer.stop();
			networkServer.stop();
		}));

		if(OSType.getOSType() == OSType.LINUX) {
			executorService.executeRepeating(new InstanceFreezer(), 12000, 12000, TimeUnit.MILLISECONDS);
		}
		executorService.executeRepeating(() -> sendToMaster(new PacketLoadUpdate()), 10, 10, TimeUnit.SECONDS);

		Logger.log(Color.DARK_GREEN + "Der Wrapper wurde gestartet!", LogType.INFO);

		logger.start();
	}

	/**
	 * @return the instance of the Wrapper.
	 */
	public static Wrapper getInstance() {
		return instance;
	}

	public CloudScheduledExecutorService getExecutorService() {
		return executorService;
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
		final DataContainer bungeeData = new DataContainer();
		packet.write(bungeeData, ServiceType.PROXY);
		proxyManager.sendPacket(bungeeData);
		final DataContainer serverData = new DataContainer();
		packet.write(serverData, ServiceType.MINECRAFT);
		serverManager.sendPacket(serverData);
	}

	public void sendToMaster(Packet packet) {
		DataContainer data = new DataContainer();
		packet.write(data, ServiceType.MASTER);
		masterClient.sendData(data);
	}

	public void sendToMaster(DataContainer container) {
		masterClient.sendData(container);
	}

	public void broadcastData(DataContainer data) {
		proxyManager.sendPacket(data);
		serverManager.sendPacket(data);
	}

	public Command[] getCommands() {
		return logger.getParser().getCommands();
	}

	public LineReader getReader() {
		return reader;
	}

	public void reload() throws IOException {
		setSetting(FileManager.execute(true, fileManager));
		final DataContainer bungeeData = new DataContainer();
		new PacketReload().write(bungeeData, ServiceType.PROXY);
		proxyManager.sendPacket(bungeeData);
	}

	public FileManager getFileManager() {
		return fileManager;
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

	public ShutdownController getShutdownController() {
		return shutdownController;
	}

	public Client getMasterConnection() {
		return masterClient;
	}

	public boolean isShutDown() {
		return shutDown;
	}
}
