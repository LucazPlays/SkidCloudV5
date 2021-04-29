package de.terrarier.terracloud;

import de.terrarier.netlistening.Server;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.netlistening.api.PacketCaching;
import de.terrarier.netlistening.api.encryption.EncryptionSetting;
import de.terrarier.netlistening.api.event.ConnectionDisconnectListener;
import de.terrarier.netlistening.api.event.ConnectionTimeoutListener;
import de.terrarier.netlistening.api.event.DecodeListener;
import de.terrarier.netlistening.impl.ApplicationImpl;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.database.MongoDataBase;
import de.terrarier.terracloud.file.FileManager;
import de.terrarier.terracloud.logging.Color;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.multithreading.CloudScheduledExecutorService;
import de.terrarier.terracloud.multithreading.CloudThreadFactory;
import de.terrarier.terracloud.networking.Packet;
import de.terrarier.terracloud.networking.PacketUnregister;
import de.terrarier.terracloud.networking.ServiceType;
import de.terrarier.terracloud.server.proxy.BungeeSetting;
import de.terrarier.terracloud.server.proxy.ProxyManager;
import de.terrarier.terracloud.server.spigot.ServerManager;
import de.terrarier.terracloud.server.wrapper.Wrapper;
import de.terrarier.terracloud.server.wrapper.WrapperManager;
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

public class Master {
	
	private static Master instance;
	private final WrapperManager wrapperManager = new WrapperManager();
	private MongoDataBase db;
	private CloudScheduledExecutorService executorService;
	private MasterSetting setting;
	private ServerManager serverManager;
	private ProxyManager proxyManager;
	private Server networkServer;
	private LineReader reader;
	private Logger logger;
	
	public Master() {
		instance = this;
	}

	/**
	 * Starts the Master.
	 *
	 */
	public void start() {
		start(SystemUtil.getAvailableProcessors() * 2, Integer.parseInt(System.getProperty("port", "20465")));
	}

	/**
	 * Starts the Master.
	 *
	 * @param threads the number of threads being in the main thread pool
	 * @param serverNetworkPort the port used for connections with Minecraft servers
	 */
	public void start(int threads, int serverNetworkPort) {
		/*if(OSType.getOSType() == OSType.WINDOWS) {
			Logger.log("Detected windows, the console may behave weirdly.", LogType.WARN);
		}*/
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
		Logger.log(Color.YELLOW + "Der Master wird gestartet...", LogType.INFO);
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
		final File srvKeyFile = new File("./Master/tmp/ServerKeys.txt");
		final byte[] serverKeys = loadKeysFromFile(srvKeyFile);
		try {
			this.networkServer = new Server.Builder(serverNetworkPort).compression().varIntCompression(true)
					.nibbleCompression(true).build().caching(PacketCaching.GLOBAL).timeout(15000)
					.encryption(new EncryptionSetting().init(serverKeys)).build();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			throw new Error("An internal error occurred while attempting to load the server encryption key!");
		}
		if(serverKeys == null) {
			saveKeysToFile(((ApplicationImpl) networkServer).getEncryptionSetting().getEncryptionData().keysToByteArray(), srvKeyFile);
		}
		this.networkServer.registerListener((DecodeListener) decodeEvent -> Packet.read(decodeEvent.getData(), decodeEvent.getConnection()));
		this.networkServer.registerListener((ConnectionTimeoutListener) connectionTimeoutEvent -> {
			final Wrapper wrapper = wrapperManager.unregisterWrapper(connectionTimeoutEvent.getConnection());
			if(wrapper != null) {
				Logger.log("Die Verbindung des Wrappers " + wrapper.getName() + " ist ausgetimed.", LogType.WARN);
			}
		});
		this.networkServer.registerListener((ConnectionDisconnectListener) connectionDisconnectEvent -> {
			final Wrapper wrapper = wrapperManager.unregisterWrapper(connectionDisconnectEvent.getConnection());
			if(wrapper != null) {
				Logger.log("Die Verbindung des Wrappers " + wrapper.getName() + " wurde unterbrochen.", LogType.INFO);
			}
		});
		this.db = new MongoDataBase();
		this.serverManager = new ServerManager();
		this.proxyManager = new ProxyManager();
		this.executorService = new CloudScheduledExecutorService(new CloudThreadFactory("TerraCloud"), threads);
		db.connect(setting.getDBSetting());
		executorService.executeAsync(() -> {
			proxyManager.initGroups();
			serverManager.initGroups();
		});

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			broadcastPacket(new PacketUnregister("", ServiceType.MASTER));
			db.disconnect();
			networkServer.stop();
		}));


		Logger.log(Color.DARK_GREEN + "Der Master wurde gestartet!", LogType.INFO);

		this.logger.start();
	}

	/**
	 * @return the instance of the Master.
	 */
	public static Master getInstance() {
		return instance;
	}
	
	public MongoDataBase getDataBase() {
		return db;
	}

	public CloudScheduledExecutorService getExecutorService() {
		return this.executorService;
	}

	public MasterSetting getSetting() {
		return this.setting;
	}

	public ServerManager getServerManager() {
		return this.serverManager;
	}

	public ProxyManager getProxyManager() {
		return this.proxyManager;
	}

	public void setSetting(MasterSetting setting) {
		this.setting = setting;
	}

	public void broadcastPacket(Packet packet) {
		broadcastPacket(packet, null);
	}

	public void broadcastPacket(Packet packet, Wrapper ignored) {
		DataContainer data = new DataContainer();
		packet.write(data, ServiceType.WRAPPER);
		for(Wrapper wrapper : wrapperManager.getWrappers()) {
			if(!wrapper.equals(ignored)) {
				wrapper.getConnection().sendData(data);
			}
		}
	}

	public void broadcastData(DataContainer dataContainer, Wrapper ignored) {
		for(Wrapper wrapper : wrapperManager.getWrappers()) {
			if(!wrapper.equals(ignored)) {
				wrapper.getConnection().sendData(dataContainer);
			}
		}
	}

	public Command[] getCommands() {
		return logger.getParser().getCommands();
	}

	public LineReader getReader() {
		return reader;
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

	public WrapperManager getWrapperManager() {
		return wrapperManager;
	}

	public void reload() {
		try {
			setting = FileManager.execute(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		final DataContainer container = new DataContainer();
		final BungeeSetting bungeeSetting = Master.getInstance().getSetting().getBungeeSetting();
		container.addAll(0x0, ServiceType.MASTER.id(), bungeeSetting.getBungeeFavicon(),
				bungeeSetting.getMotd(), bungeeSetting.getMotd2(), bungeeSetting.getSlots());
		Master.getInstance().broadcastData(container, null);
	}
}
