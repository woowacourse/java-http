package web.response;

public class StatusLine {

    private String httpVersion;
    private String statusCode;
    private String reasonPhrase;

    public StatusLine(final String httpVersion, final String statusCode, final String reasonPhrase) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public String toString() {
        return "StatusLine{" +
                "httpVersion='" + httpVersion + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", reasonPhrase='" + reasonPhrase + '\'' +
                '}';
    }
}
