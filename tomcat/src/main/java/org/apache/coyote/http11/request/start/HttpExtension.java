package org.apache.coyote.http11.request.start;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum HttpExtension {
    HTML(".html"),
    CSS(".css"),
    JS(".js"),
    QUERY("?"),
    ICO(".ico"),
    DEFAULT("")
    ;

    private final String extension;

    HttpExtension(final String extension) {
        this.extension = extension;
    }

    public static HttpExtension from(final String requestTarget) {
        return initHttpExtension().stream()
                .filter(extension -> requestTarget.contains(extension.extension))
                .findFirst()
                .orElse(HttpExtension.DEFAULT);
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
}
