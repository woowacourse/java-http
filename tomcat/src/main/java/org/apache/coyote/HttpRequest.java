package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.header.HttpHeaders;
import org.apache.coyote.header.HttpMethod;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String requestUrl;
    private final ProtocolVersion protocolVersion;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(HttpMethod httpMethod, String requestUrl, ProtocolVersion protocolVersion, HttpHeaders headers, String body) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
        this.body = body;
    }

    public String getQueryParams() {
        int index = requestUrl.indexOf("?");
        return requestUrl.substring(index + 1);
    }

    public Map<String, String> getRequestBody() {
        Map<String, String> mapped = new HashMap<>();
        for (String info : body.split("&")) {
            String[] keyValue = info.split("=");
            mapped.put(keyValue[0], keyValue[1]);
        }
        return mapped;
    }

    public String getHeaderValue(String name) {
        return headers.getValue(name);
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public String requestUrl() {
        return requestUrl;
    }

    public ProtocolVersion protocolVersion() {
        return protocolVersion;
    }

    public String body() {
        return body;
    }
}
