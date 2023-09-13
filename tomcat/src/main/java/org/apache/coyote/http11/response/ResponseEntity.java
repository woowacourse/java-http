package org.apache.coyote.http11.response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResponseEntity<T> {

    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private final int statusCode;
    private final Map<String, String> headers;
    private final T body;
    private boolean viewResponse = false;
    private String viewPath = null;

    private ResponseEntity(int statusCode, Map<String, String> headers, T body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static ResponseBuilder status(int statusCode) {
        return new ResponseBuilderImpl(statusCode);
    }

    public static ResponseEntity<Object> redirect(String path) {
        Map<String, String> headers = new HashMap<>();
        headers.put(LOCATION, path);
        return new ResponseEntity<>(302, headers, null);
    }

    public static ResponseEntity<Object> notAllowed() {
        return new ResponseEntity<>(405, Collections.emptyMap(), null);
    }

    public void responseView(String viewPath) {
        this.viewResponse = true;
        this.viewPath = viewPath;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public T getBody() {
        return body;
    }

    public boolean isViewResponse() {
        return viewResponse;
    }

    public String getViewPath() {
        return viewPath;
    }

    public interface ResponseBuilder {

        ResponseBuilder addHeader(String key, String value);

        ResponseBuilder location(String location);

        ResponseBuilder sessionCookie(String cookieId);

        <T> ResponseEntity<T> build();

        <T> ResponseEntity<T> body(T body);
    }

    private static final class ResponseBuilderImpl implements ResponseBuilder {

        private int statusCode;
        private Map<String, String> headers;

        public ResponseBuilderImpl(int statusCode) {
            this.statusCode = statusCode;
            this.headers = new HashMap<>();
        }

        @Override
        public ResponseBuilder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        @Override
        public ResponseBuilder location(String location) {
            headers.put(LOCATION, location);
            return this;
        }

        @Override
        public ResponseBuilder sessionCookie(String cookieId) {
            headers.put(SET_COOKIE, String.format("JSESSIONID=%s", cookieId));
            return this;
        }

        @Override
        public <T> ResponseEntity<T> build() {
            return body(null);
        }

        @Override
        public <T> ResponseEntity<T> body(T body) {
            return new ResponseEntity<>(this.statusCode, headers, body);
        }
    }
}
