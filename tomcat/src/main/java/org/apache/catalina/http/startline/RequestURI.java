package org.apache.catalina.http.startline;

import java.util.Objects;
import org.apache.catalina.util.ResourceReader;

public class RequestURI {

    private final String value;

    public RequestURI(String value) {
        this.value = value;
    }

    public boolean startsWith(String startsWith) {
        return value.startsWith(startsWith);
    }

    public boolean isHome() {
        return value.equals("/");
    }

    public String getValue() {
        return value;
    }

    public boolean isResource() {
        return Objects.nonNull(ResourceReader.getResource(value));
    }
}
