package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.MessageBody;

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

    public static HttpRequest from(final BufferedReader br) throws IOException {
        RequestLine requestLine = RequestLine.from(br);
        RequestHeaders requestHeaders = RequestHeaders.from(br);
        MessageBody messageBody = MessageBody.from(br);

        return new HttpRequest(requestLine, requestHeaders, messageBody);
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

    public Map<String, Object> getQueryParams() {
        return requestLine.getQueryParams();
    }
}
