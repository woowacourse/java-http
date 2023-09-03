package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
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

    public static HttpRequest from(final BufferedReader br, final String startLine) throws IOException {
        RequestLine requestLine = RequestLine.from(startLine);
        RequestHeaders requestHeaders = RequestHeaders.from(br);
        MessageBody messageBody = createRequestBody(br, requestHeaders);

        return new HttpRequest(requestLine, requestHeaders, messageBody);
    }

    private static MessageBody createRequestBody(BufferedReader br, RequestHeaders headers)
            throws IOException {
        if (headers.hasNotHeader(HttpHeaderName.CONTENT_TYPE.getValue())) {
            return MessageBody.from("");
        }
        int contentLength = Integer.parseInt((String) headers.getHeaderValue(HttpHeaderName.CONTENT_LENGTH.getValue()));
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return MessageBody.from(requestBody);
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
