package org.apache.coyote.http11;

import java.util.List;

public record RequestLine(
    HttpMethod method,
    String path,
    HttpVersion version
) {

    private static final List<String> STATIC_RESOURCE_PATHS = List.of(
        "/",
        "/401.html",
        "/assets/chart-area.js",
        "/assets/chart-bar.js",
        "/assets/chart-pie.js",
        "/css/styles.css",
        "/js/scripts.js");

    public static RequestLine from(final String rawRequestLine) {
        final String[] split = rawRequestLine.split(" ");
        final HttpMethod method = HttpMethod.valueOf(split[0]);
        final String path = PathAliasesResolver.normalize(split[1]);
        final HttpVersion version = HttpVersion.pick(split[2]);

        return new RequestLine(method, path, version);
    }

    public boolean isStaticResource() {
        return method == HttpMethod.GET && STATIC_RESOURCE_PATHS.contains(path);
    }

}
