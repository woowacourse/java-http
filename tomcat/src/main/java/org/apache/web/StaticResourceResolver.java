package org.apache.web;

import java.net.URL;

public class StaticResourceResolver {

    private static final String RESOURCE_PATH = "static";

    public ResolvedResource resolve(final String uri) {
        String path = uri;
        if (!path.matches(".*\\.(html|css|js|png|jpg)$")) {
            path += ".html";
        }

        URL url = getClass().getClassLoader().getResource(RESOURCE_PATH + path);
        if (url == null) {
            url = getClass().getClassLoader().getResource(RESOURCE_PATH + "/404.html");
        }

        String contentType = extractContentType(path);
        return new ResolvedResource(url, contentType);
    }

    private String extractContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    public record ResolvedResource(URL url, String contentType) {
    }
}
