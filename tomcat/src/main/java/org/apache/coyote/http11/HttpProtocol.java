package org.apache.coyote.http11;

public class HttpProtocol {
    private final String protocol;
    private final Float version;

    public HttpProtocol(String protocol, String version) {
        this.protocol = protocol;
        this.version = Float.valueOf(version);
    }
}
