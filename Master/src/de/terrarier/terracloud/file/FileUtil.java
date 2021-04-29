package de.terrarier.terracloud.file;

import java.io.File;

public class FileUtil {

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

}
