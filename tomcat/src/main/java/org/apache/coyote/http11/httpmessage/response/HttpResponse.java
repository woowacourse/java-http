package org.apache.coyote.http11.httpmessage.response;

import java.io.IOException;

import org.apache.coyote.http11.exception.NotCompleteResponseException;
import org.apache.coyote.http11.httpmessage.HttpCookie;
import org.apache.coyote.http11.httpmessage.HttpHeaders;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class HttpResponse {

    private final HttpCookie httpCookie;
    private final HttpStatusLine httpStatusLine;
    private final HttpHeaders headers;
    private String body;


    public HttpResponse(HttpStatusLine httpStatusLine, HttpHeaders headers, String body) {
        this.httpStatusLine = httpStatusLine;
        this.headers = headers;
        this.body = body;
        httpCookie = new HttpCookie();
    }

    public HttpResponse(HttpRequest httpRequest) {

        this(new HttpStatusLine(httpRequest.getHttpVersion(), 0, null), //todo 생성 방식 고민
                new HttpHeaders(),
                "");
    }

    public String toHttpMessage() {
        if (httpStatusLine == null) {
            throw new NotCompleteResponseException("응답이 완성되지 않았습니다.");
        }
        setCookie();
        return String.join("\r\n",
                httpStatusLine.toHttpMessage(),
                headers.toHttpMessage(),
                "",
                body);
    }

    private void setCookie() {
        if(!httpCookie.isEmpty()) {
            headers.addHeader(HttpHeaders.SET_COOKIE, httpCookie.toHttpMessage());
        }
    }

    public void setStatusFound(String target) {
        this.httpStatusLine.setStatus(HttpStatus.FOUND);
        this.headers.addHeader(HttpHeaders.LOCATION, target);
    }

    public void setStatusBadRequest() {
        this.httpStatusLine.setStatus(HttpStatus.BAD_REQUEST);
    }

    public void setResponseOfStaticResource(StaticResource resource) throws IOException {
        this.httpStatusLine.setStatus(HttpStatus.OK);
        headers.addHeader(HttpHeaders.CONTENT_TYPE, resource.getContentType() + ";charset=utf-8");
        headers.addHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(resource.getContentLength()));
        this.body = resource.getContent();
    }

    public void addCookie(String key, String value) {
        httpCookie.addCookie(key, value);
    }
}
