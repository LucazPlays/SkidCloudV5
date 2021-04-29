package de.terrarier.terracloud.file;

import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class FileUtil {

    public static final String DEFAULT_PATH = new File("none").getAbsolutePath().replaceAll("none", "");

    public static void copy(String src, String destination) throws IOException {
        Files.copy(Paths.get(src), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void insertData(String src, String destination) {
        try (InputStream localInputStream = FileManager.class.getClassLoader().getResourceAsStream(src)) {
            if (localInputStream != null) {
                Files.copy(localInputStream, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
            }else {
                Logger.log("An error occurred while copying the file from the jar!", LogType.ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFileFromURL(String urlString, File destination) {
        try {
            final URL website = new URL(urlString);
            final ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            final FileOutputStream fos = new FileOutputStream(destination);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setLine(int lineNumber, String data, String filePath) throws IOException {
        final Path path = Paths.get(filePath);
        final List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lineNumber > lines.size()) {
            return;
        }
        lines.set(lineNumber, data);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static void deleteDir(File dir) {
        try {
            if (dir == null || !dir.exists()) {
                return;
            }
            synchronized (FileUtil.class) {
                File[] files;
                if (dir.isDirectory() && (files = dir.listFiles()) != null && files.length != 0) {
                    File[] subFiles = dir.listFiles();
                    for (File f : subFiles) {
                        if (f.isDirectory()) {
                            deleteDir(f);
                        } else {
                            f.delete();
                        }
                    }
                } else {
                    dir.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFullDirectory(String from, String to) {
        File dst = new File(to);
        if (!dst.exists()) {
            dst.mkdirs();
        }
        File src = new File(from);
        File[] files = src.listFiles();
        if (files != null) {
            if (files.length != 0) {
                for (File all : files) {
                    if (!all.isDirectory()) {
                        Path p1 = Paths.get(all.getAbsolutePath());
                        Path p2 = Paths.get(to + "/" + all.getName());
                        try {
                            Files.copy(p1, p2, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        copyFullDirectory(all.getAbsolutePath(), to + "/" + all.getName());
                    }
                }
            }
        }
    }

    public static void clearDirectory(File dir) {
        final File[] files = dir.listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
        }
    }

}
