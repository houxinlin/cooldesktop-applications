package com.cooldesktop.rmi.info;

import java.io.Serializable;

public class ClassInfo implements Serializable {
    private String className;

    private String simpName;


    private String classLoaderName;


    public ClassInfo(String className, String simpName, String classLoaderName) {
        this.className = className;
        this.simpName = simpName;
        this.classLoaderName = classLoaderName;
    }


    public String getClassLoaderName() {
        return classLoaderName;
    }

    public void setClassLoaderName(String classLoaderName) {
        this.classLoaderName = classLoaderName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSimpName() {
        return simpName;
    }

    public void setSimpName(String simpName) {
        this.simpName = simpName;
    }
}
