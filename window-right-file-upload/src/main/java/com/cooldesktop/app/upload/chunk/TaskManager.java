package com.cooldesktop.app.upload.chunk;

import org.springframework.stereotype.Component;

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

    public void uninstall() {
        this.exitFlag = true;
        if (taskConsumerThread != null) taskConsumerThread.interrupt();
    }

    public TaskManager() {
        taskConsumerThread = new Thread(() -> {
            while (!exitFlag) {
                try {
                    DeleteDirectorTask take = delayQueue.take();
                } catch (InterruptedException e) {
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
