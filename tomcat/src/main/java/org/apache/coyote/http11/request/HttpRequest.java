package org.apache.coyote.http11.request;

import java.io.IOException;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.header.RequestHeaders;
import org.apache.coyote.http11.request.startLine.HttpMethod;
import org.apache.coyote.http11.request.startLine.RequestLine;

public class HttpRequest {

    private static final int EMPTY_BODY_LENGTH = 0;

    private final SessionManager sessionManager = SessionManager.getInstance();

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest read(RequestReader requestReader) throws IOException {
        RequestLine requestLine = new RequestLine(requestReader.readRequestLine());
        RequestHeaders requestHeaders = new RequestHeaders(requestReader.readRequestHeaders());

        int contentLength = getContentLength(requestHeaders);
        if (hasRequestBody(contentLength)) {
            RequestBody requestBody = createRequestBody(requestReader, requestHeaders, contentLength);
            return new HttpRequest(requestLine, requestHeaders, requestBody);
        }

        return new HttpRequest(requestLine, requestHeaders, RequestBody.empty());
    }

    private static Integer getContentLength(RequestHeaders requestHeaders) {
        return requestHeaders.get(HttpHeader.CONTENT_LENGTH.getName())
                .map(Integer::parseInt)
                .orElse(EMPTY_BODY_LENGTH);
    }

    private static boolean hasRequestBody(int contentLength) {
        return contentLength > EMPTY_BODY_LENGTH;
    }

    private static RequestBody createRequestBody(
            RequestReader requestReader, RequestHeaders requestHeaders, int contentLength) throws IOException {

        String mediaType = requestHeaders.get(HttpHeader.CONTENT_TYPE.getName())
                .orElseThrow(() -> new IllegalArgumentException("요청 헤더를 찾을 수 없습니다."));

        return new RequestBody(mediaType, requestReader.readRequestBody(contentLength));
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return requestLine.isMethod(httpMethod);
    }

    public boolean isStaticResource() {
        return requestLine.isStaticResource();
    }

    public String getParameter(String name) {
        return requestBody.get(name);
    }

    public Session getSession() {
        return getCookie().getJSessionId()
                .flatMap(sessionManager::findSession)
                .orElseGet(sessionManager::createSession);
    }

    public Cookies getCookie() {
        return requestHeaders.get(HttpHeader.COOKIE.getName())
                .map(Cookies::new)
                .orElseGet(Cookies::empty);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }
}
