package com.cooldesktop.rmi.server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
* @description: rmi 客户端
* @date: 2022/8/21 上午10:08
*/
public class CoolDesktopRMIClient {
    public IJvmCall getClient(int port, String name) {
        try {
            return ((IJvmCall) LocateRegistry.getRegistry(port).lookup(name));
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
