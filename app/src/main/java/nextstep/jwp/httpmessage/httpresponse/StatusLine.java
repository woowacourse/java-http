package nextstep.jwp.httpmessage.httpresponse;

import nextstep.jwp.httpmessage.HttpVersion;

import static nextstep.jwp.httpmessage.httprequest.HttpMessageReader.SP;

public class StatusLine {

    private final HttpVersion httpVersion;
    private HttpStatusCode httpStatusCode;

    public StatusLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getLine() {
        return httpVersion.getValue() + SP + httpStatusCode.getValue() + SP + httpStatusCode.getReasonPhrase();
    }
}
