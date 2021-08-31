package nextstep.jwp.httpmessage;

import static nextstep.jwp.httpmessage.HttpMessageReader.SP;

public class StatusLine {

    private final HttpVersion httpVersion;
    private final HttpStatusCode httpStatusCode;

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

    public String getLine() {
        return httpVersion.getValue() + SP + httpStatusCode.getValue() + SP + httpStatusCode.getReasonPhrase();
    }
}
