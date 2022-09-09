package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.util.ResourceLoader;

public enum View {

    INDEX("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    UNAUTHORIZED("/401.html"),
    NOT_FOUND("/404.html"),
    INTERNAL_SERVER_ERROR("/500.html"),
    ;

    private final String path;

    View(final String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getResource() throws IOException, URISyntaxException {
        return ResourceLoader.getStaticResource(path);
    }
}
