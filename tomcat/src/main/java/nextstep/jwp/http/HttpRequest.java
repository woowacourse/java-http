package nextstep.jwp.http;

public class HttpRequest {

    private String method;
    private String url;
    private String version;

    private HttpRequest(final String method, final String url, final String version) {
        this.method = method;
        this.url = url;
        this.version = version;
    }

    public static HttpRequest from(final String requestLine) {
        String[] requestLines = requestLine.split(" ");
        return new HttpRequest(requestLines[0], requestLines[1], requestLines[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
