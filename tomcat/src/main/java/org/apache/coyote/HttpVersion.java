package org.apache.coyote;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0");

    private final String versionString;

    HttpVersion(String versionString) {
        this.versionString = versionString;
    }

    public static HttpVersion fromString(String versionString) {
        for (HttpVersion version : HttpVersion.values()) {
            if (version.getVersionString().equals(versionString)) {
                return version;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP version: " + versionString);
    }

    public String getVersionString() {
        return versionString;
    }
}
