package nextstep.jwp.http.request;

public class HttpRequestLine {

    private static final String SEPARATOR = " ";
    private static final int REQUEST_LINE_METHOD_INDEX = 0;
    private static final int REQUEST_LINE_URI_INDEX = 1;
    private static final int REQUEST_LINE_PROTOCOL_INDEX = 2;

    private String[] splitHttpRequestLine;

    public HttpRequestLine(String httpRequestLine) {
        splitHttpRequestLine = httpRequestLine.split(SEPARATOR);
    }

    public String getMethod() {
        return splitHttpRequestLine[REQUEST_LINE_METHOD_INDEX];
    }

    public String getRequestURI() {
        return splitHttpRequestLine[REQUEST_LINE_URI_INDEX];
    }

    public String getProtocol() {
        return splitHttpRequestLine[REQUEST_LINE_PROTOCOL_INDEX];
    }
}
