package com.cooldesktop.rmi.server;

import java.lang.instrument.Instrumentation;

public class JvmInstrumentation {
    private static Instrumentation instrumentation;

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public static void setInstrumentation(Instrumentation instrumentation) {
        JvmInstrumentation.instrumentation = instrumentation;
    }
}
