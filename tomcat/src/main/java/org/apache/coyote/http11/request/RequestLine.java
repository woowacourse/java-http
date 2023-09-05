package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestLine {

    private final String method;
    private final RequestUrl requestUrl;
    private final String protocolVersion;

    public RequestLine(String method, RequestUrl requestUrl, String protocolVersion) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(String line) {
        String[] split = line.split(" ");
        return new RequestLine(split[0], new RequestUrl(split[1]), split[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return requestUrl.getUrl();
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public Map<String, String> getParams() {
        return requestUrl.getParams();
    }

    public boolean isGet() {
        return method.equals("GET");
    }

    public boolean isPost() {
        return method.equals("POST");
    }
}
