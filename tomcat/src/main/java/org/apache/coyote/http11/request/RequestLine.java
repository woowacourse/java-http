package org.apache.coyote.http11.request;

public class RequestLine {
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final RequestURI requestURI;
    private final String version;

    private RequestLine(HttpMethod httpMethod, RequestURI requestURI, String version) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.version = version;
    }

    public static RequestLine from(String request) {
        String[] elements = request.split(DELIMITER);
        return new RequestLine(
                HttpMethod.from(elements[METHOD_INDEX]),
                RequestURI.from(elements[URI_INDEX]),
                elements[HTTP_VERSION_INDEX]
        );
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return httpMethod.isEqualTo(httpMethod);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }

    public String getVersion() {
        return version;
    }

}
