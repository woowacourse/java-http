package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

import nextstep.jwp.HttpSession;

public class HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(BufferedReader reader) throws IOException {
        this.requestLine = new RequestLine(reader.readLine());
        this.requestHeaders = new RequestHeaders(reader);
        this.requestBody = parseRequestBody(reader, requestHeaders.get("Content-Length"));
    }

    private RequestBody parseRequestBody(BufferedReader reader, String length) throws IOException {
        if (length == null) {
            return null;
        }
        int contentLength = Integer.parseInt(length);
        if (contentLength == 0) {
            return null;
        }
        return new RequestBody(reader, contentLength);
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String key) {
        return requestHeaders.get(key);
    }

    public String getRequestBodyParam(String key) {
        return Optional.ofNullable(requestBody)
                       .orElseThrow(() -> new IllegalArgumentException("Request Body가 존재하지 않습니다."))
                       .getParam(key);
    }

    public boolean hasNoSessionId() {
        return !requestHeaders.hasSessionId();
    }

    public HttpSession getSession() {
        return requestHeaders.getSession();
    }
}
