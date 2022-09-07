package org.apache.coyote.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> controllerByUrl;
    private final Controller resourceController;

    private RequestMapping(final Map<String, Controller> controllerByUrl, final Controller resourceController) {
        this.controllerByUrl = controllerByUrl;
        this.resourceController = resourceController;
    }

    public static RequestMappingBuilder builder() {
        return new RequestMappingBuilder();
    }

    public Controller find(final HttpRequest httpRequest) {
        final Controller controller = controllerByUrl.get(httpRequest.getPath());
        if (controller == null) {
            return resourceController;
        }
        return controller;
    }

    public static class RequestMappingBuilder {

        private final Map<String, Controller> controllerByUrl;
        private Controller resourceController;

        public RequestMappingBuilder() {
            this.controllerByUrl = new LinkedHashMap<>();
        }

        public RequestMappingBuilder add(final String url, final Controller controller) {
            controllerByUrl.put(url, controller);
            return this;
        }

        public RequestMappingBuilder addResourceController(final Controller controller) {
            this.resourceController = controller;
            return this;
        }

        public RequestMapping build() {
            return new RequestMapping(controllerByUrl, resourceController);
        }
    }
}
