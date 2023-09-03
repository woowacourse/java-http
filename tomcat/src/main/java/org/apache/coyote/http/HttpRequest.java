package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpHeaders httpHeaders;
    private final HttpMessage httpMessage;

    public HttpRequest(HttpHeaders httpHeaders, HttpMessage httpMessage) {
        this.httpHeaders = httpHeaders;
        this.httpMessage = httpMessage;
    }

    public String getPath() {
        return httpHeaders.getPath();
    }

    public HttpHeaders getHeader() {
        return httpHeaders;
    }

    public HttpMessage getHttpMessage() {
        return httpMessage;
    }

    public Map<String, String> getParameters() {
        Map<String, String> parameters = new HashMap<>(httpHeaders.getQueryParameters());
        parameters.putAll(httpMessage.getParameters());
        return parameters;
    }
}
