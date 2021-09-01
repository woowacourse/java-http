package nextstep.jwp.httpmessage.httpresponse;

import nextstep.jwp.httpmessage.HttpHeaders;
import nextstep.jwp.httpmessage.HttpSessions;
import nextstep.jwp.httpmessage.HttpVersion;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;

import java.util.Objects;

import static nextstep.jwp.httpmessage.httprequest.HttpMessageReader.CRLF;
import static nextstep.jwp.httpmessage.httprequest.HttpMessageReader.SP;

public class HttpResponse {

    private final HttpHeaders httpHeaders;
    private HttpRequest httpRequest;
    private StatusLine statusLine;
    private Object body;

    public HttpResponse(HttpRequest httpRequest) {
        this(httpRequest, new StatusLine(HttpVersion.HTTP1_1, HttpStatusCode.OK), new HttpHeaders(), null);
    }

    public HttpResponse(HttpRequest httpRequest, StatusLine statusLine, HttpHeaders httpHeaders, Object body) {
        this.httpRequest = httpRequest;
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public HttpVersion getHttpVersion() {
        return statusLine.getHttpVersion();
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public String getStatusLineAsString() {
        return statusLine.getLine();
    }

    public boolean isSameHttpStatusCode(HttpStatusCode httpStatusCode) {
        return this.statusLine.getHttpStatusCode().equals(httpStatusCode);
    }

    public HttpStatusCode getHttpStatusCode() {
        return statusLine.getHttpStatusCode();
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.statusLine.setHttpStatusCode(httpStatusCode);
    }

    public void addHeader(String key, int value) {
        httpHeaders.setHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, String value) {
        httpHeaders.setHeader(key, value);
    }

    public String getHttpHeadersAsString() {
        return httpHeaders.getHeadersAsString();
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getHeader(String key) {
        return httpHeaders.getHeader(key);
    }

    public Object getBody() {
        if (Objects.isNull(body)) {
            return "";
        }
        return body;
    }

    public void setBody(Object body) {
        if (Objects.nonNull(body)) {
            this.body = body;
        }
    }

    public String getHttpMessage() {
        if (Objects.nonNull(httpRequest)) {
            setCookie();
        }

        return String.join(CRLF,
                statusLine.getLine() + SP,
                httpHeaders.getHeadersAsString(),
                getBody().toString());
    }

    public void setCookie(String key, String value) {
        httpHeaders.setCookie(key, value);
    }

    private void setCookie() {
        if (httpRequest.hasSession() && Objects.isNull(HttpSessions.getSession(httpRequest.getHttpSessionId()))) {
            HttpSessions.addSession(httpRequest.getHttpSession());
            httpHeaders.setSessionId(httpRequest.getHttpSessionId());
        }
    }
}
