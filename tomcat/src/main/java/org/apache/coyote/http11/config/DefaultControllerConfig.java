package org.apache.coyote.http11.config;

import org.apache.coyote.http11.controller.ControllerProvider;
import org.apache.coyote.http11.controller.StaticResourceController;

public class DefaultControllerConfig {

    private DefaultControllerConfig() {
    }

    public static void initialize() {
        configStaticResourceController();
    }

    private static void configStaticResourceController() {
        ControllerProvider.INSTANCE.register(new StaticResourceController());
    }
}
