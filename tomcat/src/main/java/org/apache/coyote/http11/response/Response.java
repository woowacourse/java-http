package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class Response<T> {

    private int statusCode = 200;
    private Map<String, String> headers;
    private T body;
    private boolean viewResponse = false;
    private String viewPath = null;

    private Response(int statusCode, Map<String, String> headers, T body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static ResponseBuilder status(int statusCode) {
        return new ResponseBuilderImpl(statusCode);
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

        <T> Response<T> build();

        <T> Response<T> body(T body);
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
        public <T> Response<T> build() {
            return body(null);
        }

        @Override
        public <T> Response<T> body(T body) {
            return new Response<>(this.statusCode, headers, body);
        }
    }
}
