package org.apache.catalina.resources;

public class ViewResourceManager extends ResourceManager {

    private static final String HTML_EXTENSION = ".html";

    @Override
    public String resolve(final String resourcePath) {
        return resourcePath.endsWith(HTML_EXTENSION)
                ? resourcePath
                : resourcePath + HTML_EXTENSION;
    }
}
