package org.apache.coyote;

public enum HttpVersion {

    HTTP1_1("HTTP/1.1");

    private final String name;

    HttpVersion(String name) {
        this.name = name;
    }

    public static HttpVersion fromString(String version) {
        for (HttpVersion httpVersion : values()) {
            if (httpVersion.name.equals(version)) {
                return httpVersion;
            }
        }
        throw new IllegalArgumentException("Cannot resolve Http Version from request: " + version);
    }

    public String getName() {
        return name;
    }
}
