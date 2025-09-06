package org.apache.coyote.http11.request;

public class MappingLine { // GET /endPoint HTTP/1.1

    private final String requestMapping; // GET
    private final String url; // /endPoint
    private final String protocol; // HTTP/1.1

    public MappingLine(String requestMapping, String url, String protocol) {
        this.requestMapping = requestMapping;
        this.url = url;
        this.protocol = protocol;
    }

    public MappingLine(String url, String requestMapping) {
        this(url, requestMapping, "HTTP/1.1");
    }

    @Override
    public String toString() {
        return String.join(" ",
                requestMapping,
                url,
                protocol
        );
    }
}
