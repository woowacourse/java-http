package org.apache.catalina.resource;

import java.util.Objects;

public class ViewResourceLoader extends ResourceLoader {

    private static final String HTML_EXTENSION = ".html";

    @Override
    public String resolve(String resourcePath) {
        if (Objects.equals(resourcePath, "/")) {
            return resourcePath;
        }

        return resourcePath.endsWith(HTML_EXTENSION)
                ? resourcePath
                : resourcePath + HTML_EXTENSION;

    }
}
