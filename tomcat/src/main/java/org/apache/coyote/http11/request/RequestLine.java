package org.apache.coyote.http11.request;

import org.apache.coyote.http11.utils.UrlParser;

public class RequestLine {
    private final HttpMethod httpMethod;
    private final String path;
    private final String protocolVersion;

    public RequestLine(HttpMethod httpMethod, String path, String protocolVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

//    private String updateIfNoDot(String path) {
//        int index = path.indexOf(".");
//        if (existsNotExtension(index)) {
//            return path + "." + TEXT_HTML.getExtension();
//        }
//        return path;
//    }

    private boolean existsNotExtension(int index) {
        return index == -1;
    }

    public static RequestLine extract(String request) {
        return new RequestLine(
                UrlParser.extractMethod(request),
                UrlParser.extractPath(request),
                UrlParser.extractProtocolVersion(request));

    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "httpMethod=" + httpMethod +
                ", path='" + path + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                '}';
    }
}
