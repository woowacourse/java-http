package nextstep.jwp.http.response;

public class HttpResponseLine {

    private String version;
    private String statusCode;
    private String statusMessage;

    public HttpResponseLine(final String version, final String statusCode, final String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public static HttpResponseLine from(final String requestLine) {
        String[] requestLines = requestLine.split(" ");
        return new HttpResponseLine(requestLines[0], requestLines[1], requestLines[2]);
    }
}
