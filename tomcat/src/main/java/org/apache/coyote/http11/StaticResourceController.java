package org.apache.coyote.http11;

public class StaticResourceController implements Controller {

    private static final String EMPTY_URI = "/";
    private static final String DEFAULT_URI = "/index.html";

    private static StaticResourceController instance = new StaticResourceController();

    private StaticResourceController() {
    }

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public String process(String uri) {
        if (EMPTY_URI.equals(uri)) {
            return DEFAULT_URI;
        }
        return uri;
    }
}
