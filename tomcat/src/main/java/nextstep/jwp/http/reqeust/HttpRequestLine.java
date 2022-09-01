package nextstep.jwp.http.reqeust;

public class HttpRequestLine {

    private String method;
    private String url;
    private String version;

    private HttpRequestLine(final String method, final String url, final String version) {
        this.method = method;
        this.url = url;
        this.version = version;
    }

    public static HttpRequestLine from(final String requestLine) {
        String[] requestLines = requestLine.split(" ");
        return new HttpRequestLine(requestLines[0], requestLines[1], requestLines[2]);
    }

    public String getUrl() {
        return url;
    }
}
