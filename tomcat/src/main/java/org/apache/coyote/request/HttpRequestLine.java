package org.apache.coyote.request;


public class HttpRequestLine {

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String SEPERATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final String method;
    private final RequestUri requestUri;
    private final String version;

    public HttpRequestLine(String method, RequestUri requestUri, String version) {
        this.method = method;
        this.requestUri = requestUri;
        this.version = version;
    }

    public static HttpRequestLine from(String requestLine) {
        String[] splitedLines = requestLine.split(SEPERATOR);

        return new HttpRequestLine(splitedLines[METHOD_INDEX],
                RequestUri.from(splitedLines[REQUEST_URI_INDEX]), splitedLines[VERSION_INDEX]);
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public boolean isGetMethod() {
        return method.equals(GET);
    }

    public boolean isPostMethod() {
        return method.equals(POST);
    }
}
