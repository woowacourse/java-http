package org.apache.coyote;

import org.apache.catalina.servlets.ControllerMappings;
import org.apache.coyote.http11.ResourceLocator;

public class WebConfig {

    private final ResourceLocator resourceLocator;
    private final ControllerMappings controllerMappings;

    public WebConfig(ResourceLocator resourceLocator, ControllerMappings controllerMappings) {
        this.resourceLocator = resourceLocator;
        this.controllerMappings = controllerMappings;
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    public ControllerMappings getControllerMappings() {
        return controllerMappings;
    }
}
