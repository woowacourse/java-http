package org.apache.coyote.http11.request.start;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum HttpExtension {
    HTML(".html", "default"),
    CSS(".css","default"),
    JS(".js","default"),
    ICO(".ico","default"),
    QUERY("?","dispatcher"),
    DEFAULT("","dispatcher")
    ;

    private final String extension;
    private final String servletType;

    HttpExtension(final String extension, final String servletType) {
        this.extension = extension;
        this.servletType = servletType;
    }

    public static HttpExtension from(final String requestTarget) {
        return initHttpExtension().stream()
                .filter(extension -> requestTarget.contains(extension.extension))
                .findFirst()
                .orElse(DEFAULT);
    }

    public String findResources(final String requestTarget) {
        if (this.equals(DEFAULT)) {
            return requestTarget;
        }
        return requestTarget.substring(0, requestTarget.indexOf(this.extension));
    }

    private static List<HttpExtension> initHttpExtension() {
        final List<HttpExtension> httpExtensions = Arrays.stream(HttpExtension.values()).collect(Collectors.toList());
        httpExtensions.remove(DEFAULT);
        return httpExtensions;
    }

    public String getExtension() {
        return extension;
    }

    public String getServletType() {
        return servletType;
    }
}
