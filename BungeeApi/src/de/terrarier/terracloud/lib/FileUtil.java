package de.terrarier.terracloud.lib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class FileUtil {

    public static void copy(File src, File destination) throws IOException {
        final File[] files = src.listFiles();
        if(!destination.exists()) {
            destination.mkdirs();
        }
        if(files != null && files.length != 0) {
            for (File file : files) {
                if(file.isDirectory()) {
                    copy(file, new File(destination.getAbsolutePath() + "/" + file.getName()));
                }else {
                    Files.copy(file.toPath(), Paths.get(destination.getAbsolutePath() + "/" + file.getName()),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

}
