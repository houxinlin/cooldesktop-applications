package com.cooldesktop.app.upload.chunk;

import com.cooldesktop.app.upload.utils.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Component
public class TaskManager {
    private static final long MAIN_WAIT = TimeUnit.HOURS.toMillis(1);
    private ConcurrentHashMap<String, DeleteDirectorTask> uploadIdMap = new ConcurrentHashMap<>();
    private DelayQueue<DeleteDirectorTask> delayQueue = new DelayQueue<>();
    private volatile boolean exitFlag = false;
    private Thread taskConsumerThread = null;

    /**
     * 卸载APP
     */
    public void uninstall() {
        this.exitFlag = true;
        if (taskConsumerThread != null) taskConsumerThread.interrupt();
        //删除文件存储目录
        try {
            FileSystemUtils.deleteRecursively(Paths.get(FileUtils.getStorageWorkPath()));
            Files.deleteIfExists(Paths.get(FileUtils.getConfigWorkPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TaskManager() {
        taskConsumerThread = new Thread(() -> {
            while (!exitFlag) {
                try {
                    DeleteDirectorTask take = delayQueue.take();
                    //删除临时目录
                    FileSystemUtils.deleteRecursively(Paths.get(FileUtils.getStorageWorkPath(), take.uuid));
                } catch (InterruptedException | IOException e) {
                }
            }
        });
        taskConsumerThread.start();
    }

    public String generatorTaskId() {
        String uuId = UUID.randomUUID().toString();
        DeleteDirectorTask remoteDirectorTask = new DeleteDirectorTask(MAIN_WAIT, uuId);
        uploadIdMap.put(uuId, remoteDirectorTask);
        delayQueue.add(remoteDirectorTask);
        return uuId;
    }

    public DeleteDirectorTask getDeleteDirectorTask(String taskId) {
        return uploadIdMap.get(taskId);
    }

    class DeleteDirectorTask implements Delayed {
        private long expire;
        private String uuid;

        public DeleteDirectorTask(long expire, String uuid) {
            this.expire = System.currentTimeMillis() + expire;

            this.uuid = uuid;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return this.expire - System.currentTimeMillis();
        }

        @Override
        public int compareTo(Delayed o) {
            return 0;
        }
    }
}
