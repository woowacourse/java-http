package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    private static final Map<String, HttpVersion> versions =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(HttpVersion::getVersion, Function.identity()))
            );

    public static HttpVersion find(String version) {
        if (!versions.containsKey(version)) {
            throw new IllegalArgumentException("존재하지 않는 Http 버전 입니다.");
        }
        return versions.get(version);
    }

    public String getVersion() {
        return version;
    }
}


