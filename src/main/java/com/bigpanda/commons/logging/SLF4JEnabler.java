package com.bigpanda.commons.logging;

public class SLF4JEnabler {

    public static void enable() {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    }
}
