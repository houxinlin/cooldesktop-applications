package com.cooldesktop.rmi.server;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class CoolDesktopRMIServer {
    private static final Logger log = Logger.getLogger(CoolDesktopRMIServer.class.getName());
    private static Registry registry;
    private static Remote remote = null;

    private static JvmCallServer jvmInfoServer = new JvmCallServer();


    /**
     * @description: 创建RMI对象
     * @date: 2022/8/21 下午5:08
     */

    public void register(int port, String name) {
        try {
            registry = createRegistry(port);
            if (registry != null) {
                jvmInfoServer=new JvmCallServer();
                remote = UnicastRemoteObject.exportObject(jvmInfoServer, 0);
                registry.bind(name, remote);
                log.info("cooldesktop rmi bin success");
            }
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Registry createRegistry(int port) {
        try {
            return LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            log.warning("create registry fail" + e.getMessage());
            return null;
        }
    }

    public void unbind() {
        if (registry != null) {
            try {
                for (String s : registry.list()) {
                    registry.unbind(s);
                }
                UnicastRemoteObject.unexportObject(registry, true);
                remote = null;
                jvmInfoServer=null;
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
