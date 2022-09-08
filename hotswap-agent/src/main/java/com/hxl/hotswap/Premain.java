package com.hxl.hotswap;

import com.cooldesktop.rmi.server.CoolDesktopRMIServer;
import com.cooldesktop.rmi.server.JvmInstrumentation;

import java.io.IOException;
import java.lang.instrument.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.logging.Logger;

public class Premain {
    private static final Logger log = Logger.getLogger(Premain.class.getName());
    private static final String ARG_RMI_PORT = "rmi-port";
    private static final String ARG_RMI_NAME = "rmi-name";
    private static final String ARG_ACTION_NAME = "action";
    private static final String CLOSE_RMI_SERVER="close-rmi";
    private static CoolDesktopRMIServer coolDesktopRMIServer = new CoolDesktopRMIServer();

    public static void agentmain(String agentArgs, Instrumentation inst) {
        log.info("cooldesktop agent call success ["+agentArgs+"]");
        JvmInstrumentation.setInstrumentation(inst);
        Map<String, String> arg = StringUtils.parseUrlArg(agentArgs);
        //如果是关闭rmi消息
        if (arg.getOrDefault(ARG_ACTION_NAME,"").equals(CLOSE_RMI_SERVER)){
            coolDesktopRMIServer.unbind();
            log.info("rmi close success");
            return;
        }
        if (arg.containsKey(ARG_RMI_PORT) && arg.containsKey(ARG_RMI_NAME)) {
            coolDesktopRMIServer.register(Integer.parseInt(arg.get(ARG_RMI_PORT)), arg.get(ARG_RMI_NAME));
            log.info("cooldesktop rmi register success");
            return;
        }
        log.warning("参数错误");
    }

    public static void premain(String agentArgs, Instrumentation inst) {
//        start(agentArgs, inst);
    }

}
