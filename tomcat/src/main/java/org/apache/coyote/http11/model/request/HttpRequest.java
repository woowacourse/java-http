package org.apache.coyote.http11.model.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final String body;

    public HttpRequest(final HttpRequestLine requestLine, final String body) {
        this.requestLine = requestLine;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) {
        try{
            HttpRequestLine requestLine =  HttpRequestLine.of(reader.readLine());
            String body = createBody(reader);
            return new HttpRequest(requestLine, body);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Request에 생성 시, 문제가 발생했습니다.");
        }
    }

    private static String createBody(final BufferedReader reader) throws IOException {
        StringBuilder body = new StringBuilder();
        while(reader.ready()) {
            body.append(reader.readLine());
        }
        return body.toString();
    }

    public boolean isEmptyQueryParams() {
        return requestLine.isEmptyQueryParams();
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getParams();
    }

    public String getRequestTarget() {
        return requestLine.getTarget();
    }
}
