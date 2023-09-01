package org.apache.coyote.http11;

public class UriRequest {

    private final String method;
    private final String url;
    private final String protocol;

    public UriRequest(String method, String url, String protocol) {
        this.method = method;
        this.url = url;
        this.protocol = protocol;
    }

    public static UriRequest of(String line) {
        String[] split = line.split(" ");
        return new UriRequest(split[0], split[1], split[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }
}
