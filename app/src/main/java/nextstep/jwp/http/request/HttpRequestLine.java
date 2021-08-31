package nextstep.jwp.http.request;

public class HttpRequestLine {

    private static final String SEPARATOR = " ";

    private String[] splitHttpRequestLine;

    public HttpRequestLine(String httpRequestLine) {
        splitHttpRequestLine = httpRequestLine.split(SEPARATOR);
    }

    public String getMethod() {
        return splitHttpRequestLine[0];
    }

    public String getRequestURI() {
        return splitHttpRequestLine[1];
    }

    public String getProtocol() {
        return splitHttpRequestLine[2];
    }
}
