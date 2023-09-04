package org.apache.coyote.http11;

import java.util.Map;

public class RequestUri {

    private final String method;
    private final RequestUrl requestUrl;
    private final String protocol;

    public RequestUri(String method, RequestUrl requestUrl, String protocol) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.protocol = protocol;
    }

    public static RequestUri of(String line) {
        String[] split = line.split(" ");
        return new RequestUri(split[0], new RequestUrl(split[1]), split[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return requestUrl.getUrl();
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getParams() {
        return requestUrl.getParams();
    }
}
