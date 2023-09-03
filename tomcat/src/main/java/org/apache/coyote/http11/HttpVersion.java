package org.apache.coyote.http11;

import org.apache.coyote.http11.parser.HttpFormatException;

public enum HttpVersion {
    V1_1("1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion convertFrom(String httpVersion) {
        if (!httpVersion.toUpperCase().equals(httpVersion)) {
            throw new HttpFormatException();
        }
        for (HttpVersion version : values()) {
            if (version.version.equals(httpVersion)) {
                return version;
            }
        }
        throw new HttpFormatException();
    }
}
