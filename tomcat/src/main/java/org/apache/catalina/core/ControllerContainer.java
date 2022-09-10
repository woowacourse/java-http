package org.apache.catalina.core;

public class ControllerContainer {

    private final RequestMapping requestMapping;

    public ControllerContainer(final Configuration configuration) {
        this.requestMapping = new RequestMapping();
        configuration.addController(requestMapping);
    }

    public Controller getController(final String path) {
        return requestMapping.getController(path);
    }
}
