package org.apache.coyote.http11.message.response;

import java.io.IOException;
import org.apache.catalina.Session;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpHeader;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    protected static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String NEWLINE = "\r\n";
    private static final String END_OF_HEADERS = "";
    private static final String JSESSIONID = "JSESSIONID=";
    private static final String NO_CONTENT_LENGTH = "0";
    private static final String HTTP_PROTOCOL = "http://";

    private StatusLine statusLine;
    private HttpHeaders headers;
    private String body = "";

    public HttpResponse() {
        headers = new HttpHeaders();
        addHeader(HttpHeader.CONTENT_LENGTH, NO_CONTENT_LENGTH);
    }

    public void setStatus(StatusCode statusCode) {
        statusLine = new StatusLine(HTTP_VERSION, statusCode);
    }

    public void addHeader(HttpHeader header, String value) {
        headers.add(header, value);
    }

    public void addCookie(Session session) {
        log.info("쿠키에 세션 등록!! {}", session.getId());
        headers.add(HttpHeader.SET_COOKIE, JSESSIONID + session.getId());
    }

    public void setBody(String body) {
        headers.add(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public void setView(String url) throws IOException {
        addHeader(HttpHeader.CONTENT_TYPE, ContentType.from(url).getValue());
        setBody(ViewResolver.getInstance().resolveViewName(url));
    }

    public void sendRedirect(HttpRequest httpRequest, String url) {
        String host = httpRequest.getHost();
        addHeader(HttpHeader.LOCATION, HTTP_PROTOCOL + host + url);
        addHeader(HttpHeader.CONTENT_TYPE, ContentType.from(url).getValue());
        addHeader(HttpHeader.CONTENT_LENGTH, NO_CONTENT_LENGTH);
    }

    public String convertMessage() {
        return String.join(
                NEWLINE,
                statusLine.convertMessage(),
                headers.convertMessage(),
                END_OF_HEADERS,
                body
        );
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", bodyLength='" + body.getBytes().length + '\'' +
                '}';
    }
}
