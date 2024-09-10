package org.apache.catalina.http.startline;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import org.apache.catalina.exception.CatalinaException;

public class RequestUri {

    private final String value;

    public RequestUri(String value) {
        this.value = value;
    }

    public boolean startsWith(String startsWith) {
        return value.startsWith(startsWith);
    }

    public Path getPath() {
        URL resource = getResource();
        if (resource == null) {
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
            path = path + ".html";
        }

        return getClass().getClassLoader().getResource("static" + path);
    }

    public boolean isResource() {
        return getResource() != null;
    }

    public boolean isBlank() {
        return value.isBlank() || value.equals("/");
    }
}
