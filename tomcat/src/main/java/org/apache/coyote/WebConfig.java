package org.apache.coyote;

import org.apache.catalina.servlets.RequestMappings;
import org.apache.coyote.http11.ResourceLocator;

public class WebConfig {

    private final ResourceLocator resourceLocator;
    private final RequestMappings requestMappings;

    public WebConfig(ResourceLocator resourceLocator, RequestMappings requestMappings) {
        this.resourceLocator = resourceLocator;
        this.requestMappings = requestMappings;
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    public RequestMappings getControllerMappings() {
        return requestMappings;
    }
}
