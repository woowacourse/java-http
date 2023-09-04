package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseEntity {
    private final String path;
    private final HttpStatusCode httpStatusCode;
    private final Map<String, String> additionalHeader = new HashMap<>();

    private HttpResponseEntity(final String path, final HttpStatusCode httpStatusCode) {
        this.path = path;
        this.httpStatusCode = httpStatusCode;
    }

    public static HttpResponseEntity ok(final String path) {
        return new HttpResponseEntity(path, HttpStatusCode.OK);
    }

    public static HttpResponseEntity found(final String path) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity(path, HttpStatusCode.FOUND);
        httpResponseEntity.additionalHeader.put("Location: ", path);
        return httpResponseEntity;
    }

    public void addHeader(final String name, final String value) {
        additionalHeader.put(name, value);
    }

    public String getPath() {
        return path;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public Map<String, String> getAdditionalHeader() {
        return additionalHeader;
    }
}
