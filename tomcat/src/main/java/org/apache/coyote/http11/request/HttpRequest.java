package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.security.Cookie;
import org.apache.coyote.http11.security.Session;
import org.apache.coyote.http11.security.SessionManager;

public class HttpRequest {

    private static final String CHARSET_UTF_8 = ";charset=utf-8";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final MessageBody messageBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders, final MessageBody messageBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.messageBody = messageBody;
    }

    public static HttpRequest from(final BufferedReader br, final String startLine) throws IOException {
        RequestLine requestLine = RequestLine.from(startLine);
        RequestHeaders requestHeaders = RequestHeaders.from(br);
        MessageBody messageBody = createRequestBody(br, requestHeaders);

        return new HttpRequest(requestLine, requestHeaders, messageBody);
    }

    private static MessageBody createRequestBody(BufferedReader br, RequestHeaders headers) throws IOException {
        if (headers.hasNotHeader(HttpHeaderName.CONTENT_TYPE.getValue())) {
            return MessageBody.empty();
        }
        return MessageBody.from(br.readLine());
    }

    public boolean isParamRequest() {
        return requestLine.getRequestUri().isContainsQueryParam();
    }

    public String getHeader(final String headerKey) {
        return requestHeaders.getHeaderValue(headerKey);
    }

    public Session getSession(final boolean create) {
        SessionManager sessionManager = new SessionManager();

        if (create) {
            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            return session;
        }

        String cookieValue = requestHeaders.getHeaderValue(HttpHeaderName.COOKIE.getValue());
        Cookie cookie = Cookie.from(cookieValue);
        return sessionManager.findSession(cookie.getCookieValue("JSESSIONID"));
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public HttpVersion getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri().getValue();
    }

    public String getContentType() {
        RequestUri requestUri = requestLine.getRequestUri();
        if (requestUri.isCssUri()) {
            return ContentType.TEXT_CSS.getValue() + CHARSET_UTF_8;
        }
        if (requestUri.isJavaScriptUri()) {
            return ContentType.APPLICATION_JAVASCRIPT.getValue() + CHARSET_UTF_8;
        }
        return ContentType.TEXT_HTML.getValue() + CHARSET_UTF_8;
    }

    public String getMappingUri() {
        return requestLine.getMappingUri();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public MessageBody getMessageBody() {
        return messageBody;
    }

    public Map<String, Object> getQueryParams() {
        return requestLine.getQueryParams();
    }
}
