package nextstep.jwp.httpmessage;

import java.util.Objects;

import static nextstep.jwp.httpmessage.HttpMessageReader.CRLF;
import static nextstep.jwp.httpmessage.HttpMessageReader.SP;

public class HttpResponse {

    private StatusLine statusLine;
    private HttpHeaders httpHeaders;
    private Object body;

    public HttpResponse() {
        this(new StatusLine(HttpVersion.HTTP1_1, HttpStatusCode.OK), new HttpHeaders(), null);
    }

    public HttpResponse(StatusLine statusLine, HttpHeaders httpHeaders, Object body) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public HttpVersion getHttpVersion() {
        return statusLine.getHttpVersion();
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public String getStatusLineAsString() {
        return statusLine.getLine();
    }

    public HttpStatusCode getHttpStatusCode() {
        return statusLine.getHttpStatusCode();
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

    public void setBody(Object body) {
        if (Objects.nonNull(body)) {
            this.body = body;
        }
    }

    public Object getBody() {
        if (Objects.isNull(body)) {
            return "";
        }
        return body;
    }

    public String getHttpMessage() {
        return String.join(CRLF,
                statusLine.getLine() + SP,
                httpHeaders.getHeadersAsString(),
                getBody().toString());
    }
}
