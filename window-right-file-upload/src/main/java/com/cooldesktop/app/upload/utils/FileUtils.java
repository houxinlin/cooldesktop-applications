package com.cooldesktop.app.upload.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    public static String getConfigWorkPath() {
        try {
            Path path = Paths.get(System.getProperty("user.dir"), "work", "config");
            if (!Files.exists(path)) Files.createDirectories(path);
            return Paths.get(path.toString(), "right-upload-token.cfg").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getStorageWorkPath() {
        try {
            Path path = Paths.get(System.getProperty("user.dir"), "work", "right-file-upload");
            if (!Files.exists(path)) Files.createDirectories(path);
            return path.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createTaskDirectory(String taskId) {
        Path path = Paths.get(getStorageWorkPath(), taskId);
        try {
            if (!Files.exists(path)) Files.createDirectories(path);
        } catch (Exception e) {
        }
        return path.toString();
    }

    public static long fileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static ByteBuffer readFile(Path path) {
        try {
            return ByteBuffer.wrap(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path getStoragePath(String taskDirectory, String fileName) {
        String parent = new File(taskDirectory).getParentFile().getAbsolutePath();
        Path path = Paths.get(parent, fileName);
        if (!Files.exists(path)) return path;
        int count = 1;
        while (Files.exists(path)) {
            path = Paths.get(parent, count + "-" + fileName);
            count++;
        }
        return path;
    }

    public static void merge(Path dir, Path target) {
        RandomAccessFile randomAccessFile = null;
        try {
            List<Path> files = Files.list(dir).collect(Collectors.toList());
            if (!Files.exists(target)) Files.createFile(target);
            randomAccessFile = new RandomAccessFile(target.toString(), "rw");
            for (Path path : files) {
                ByteBuffer byteBuffer = readFile(path);
                try {
                    randomAccessFile.getChannel().write(byteBuffer);
                    delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Files.deleteIfExists(dir);
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
