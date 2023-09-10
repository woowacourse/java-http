package nextstep.jwp.response;

import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.common.HttpVersion;

public class StatusLine {

    private HttpVersion httpVersion;
    private int statusCode;
    private String responsePhrase;

    private StatusLine(final HttpVersion httpVersion, final int statusCode, final String responsePhrase) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.responsePhrase = responsePhrase;
    }

    public static StatusLine of(final HttpVersion httpVersion, final HttpStatus httpStatus) {
        final int statusCode = httpStatus.getStatusCode();
        final String responsePhrase = httpStatus.name();
        return new StatusLine(httpVersion, statusCode, responsePhrase);
    }

    public String toResponse() {
        return httpVersion.getVersion() + " " + statusCode + " " + responsePhrase + " ";
    }
}
