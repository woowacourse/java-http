package org.apache.coyote.http11.header;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.StatusCode;

public class HttpResponseHeader {
    private StatusCode statusCode;
    private final Map<HeaderContent, String> payLoads;

    public HttpResponseHeader() {
        payLoads = new LinkedHashMap<>();
    }

    public void put(HeaderContent content, String value) {
        payLoads.put(content, value);
    }

    public void statusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public Map<HeaderContent, String> getPayLoads() {
        return payLoads;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}

