package nextstep.jwp.model;

public class StatusLine {

    private final String httpVersion;
    private final int statusCode;
    private final String reasonPhrase;

    public StatusLine(final String httpVersion, final int statusCode, final String reasonPhrase) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String getResponse() {
        return String.join(" ",
                httpVersion,
                String.valueOf(statusCode),
                reasonPhrase);
    }
}
