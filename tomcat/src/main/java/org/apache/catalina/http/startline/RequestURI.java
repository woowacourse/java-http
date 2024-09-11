package org.apache.catalina.http.startline;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.catalina.exception.CatalinaException;

public class RequestURI {

    public static final String HTML_EXTENSION = ".html";
    private final String value;

    public RequestURI(String value) {
        this.value = value;
    }

    public boolean startsWith(String startsWith) {
        return value.startsWith(startsWith);
    }

    public Path getPath() {
        URL resource = getResource();
        if (Objects.isNull(resource)) {
            throw new CatalinaException("Could not find resource: " + value);
        }

        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new CatalinaException("Cannot convert to URI: " + resource);
        }
    }

    private URL getResource() {
        String path = value;
        if (!path.contains(".")) {
            path = path + HTML_EXTENSION;
        }

        return getClass().getClassLoader().getResource("static" + path);
    }

    public boolean isResource() {
        return Objects.nonNull(getResource());
    }

    public boolean isHome() {
        return value.equals("/");
    }
}
