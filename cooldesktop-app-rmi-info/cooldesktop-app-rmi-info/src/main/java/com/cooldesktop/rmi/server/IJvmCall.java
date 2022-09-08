package com.cooldesktop.rmi.server;

import com.cooldesktop.rmi.info.ClassInfo;
import com.cooldesktop.rmi.info.ClassInstanceInfo;
import com.cooldesktop.rmi.info.CoolThreadInfo;
import com.cooldesktop.rmi.info.JvmInfos;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IJvmCall extends Remote {

    /**
     * @description: 获取jvm信息
     * @date: 2022/8/21 下午4:26
     */
    public JvmInfos getJvmInfos() throws RemoteException;

    public String hotswap(String classPath,byte[] clasFile) throws RemoteException;

    public CoolThreadInfo getThreadInfo(long threadId)throws RemoteException;

    public Map<String,List<ClassInfo>> getAllLoadedClass()throws RemoteException;

    public String  dumpClass(String classLoaderName,String className)throws RemoteException;

    public boolean gc()throws RemoteException;
    public List<ClassInstanceInfo> getClassInstanceInfo()throws RemoteException;

}
