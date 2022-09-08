package com.cooldesktop.rmi.info;

import java.io.Serializable;

public class ClassInstanceInfo implements Serializable {
    private String className;
    private int instances;
    private long bytes;

    public ClassInstanceInfo() {
    }

    public ClassInstanceInfo(String className, int instances, long bytes) {
        this.className = className;
        this.instances = instances;
        this.bytes = bytes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }
}
