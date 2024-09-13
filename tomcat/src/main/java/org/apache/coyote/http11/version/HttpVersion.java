package org.apache.coyote.http11.version;

import org.apache.coyote.http11.method.NotFoundHttpMethodException;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private final String version;

    private static final Map<String, HttpVersion> CLASSIFY =
            Arrays.stream(HttpVersion.values())
                    .collect(Collectors.toMap(HttpVersion::getVersion, Function.identity()));

    HttpVersion(final String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public static HttpVersion from(final String version) {
        final String v = version.toUpperCase();
        return Optional.ofNullable(CLASSIFY.get(v))
                .orElseThrow(() -> new NotFoundHttpMethodException(String.format("%s is Not Support!", v)));
    }
}
