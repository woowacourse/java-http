package org.apache.catalina.resources;

import java.util.Objects;

public class ViewResourceManager extends ResourceManager {

    private static final String HTML_EXTENSION = ".html";

    @Override
    public String resolve(final String resourcePath) {
        if (Objects.equals(resourcePath, "/")) {
            return resourcePath;
        }

        return resourcePath.endsWith(HTML_EXTENSION)
                ? resourcePath
                : resourcePath + HTML_EXTENSION;
    }
}
