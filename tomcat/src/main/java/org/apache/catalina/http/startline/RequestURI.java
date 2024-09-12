package org.apache.catalina.http.startline;

import java.util.Objects;
import org.apache.catalina.util.ResourceReader;

public record RequestURI(String value) {

    public boolean startsWith(String startsWith) {
        return value.startsWith(startsWith);
    }

    public boolean isRoot() {
        return value.equals("/");
    }

    public boolean isResource() {
        return Objects.nonNull(ResourceReader.getResource(value));
    }
}
