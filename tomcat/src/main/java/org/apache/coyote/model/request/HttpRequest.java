package org.apache.coyote.model.request;

import java.io.BufferedReader;

public class HttpRequest {

    private final RequestLine requestLine;
    private final String requestBody;

    private HttpRequest(RequestLine requestLine, String requestBody) {
        this.requestLine = requestLine;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final BufferedReader reader) {
        try {
            RequestLine requestLine = RequestLine.of(reader.readLine());
            String requestBody = createBody(reader);
            return new HttpRequest(requestLine, requestBody);
        } catch (Exception e) {
            throw new IllegalArgumentException("Request 생성 오류");
        }
    }

    private static String createBody(BufferedReader reader) {
        try {
            StringBuilder body = new StringBuilder();
            while (reader.ready()) {
                body.append(reader.readLine());
            }
            return body.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Request Body 생성 오류");
        }
    }

    public Param getParams() {
        return requestLine.getParams();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Method getHttpMethod() {
        return requestLine.getMethod();
    }

    public boolean checkEmptyParams() {
        return requestLine.isEmptyParam();
    }
}
