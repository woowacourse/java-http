package org.apache.coyote.http11;

public class HttpRequest {

    private RequestLine requestLine;
    private Headers headers;
    private String body;
    private RequestParameters requestParameters;

    public HttpRequest(final String startLine) {
        String[] splitStartLine = startLine.split(" ");
        this.requestLine = new RequestLine(splitStartLine[0], splitStartLine[1], splitStartLine[2]);
        this.headers = new Headers();
        this.body = "";
        this.requestParameters = RequestParameters.EMPTY_PARAMETERS;
    }

    public String getRequestLine() {
        return requestLine.getHttpMethod() + " " + getPath() + " " + requestLine.getHttpVersion().getValue();
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return requestLine.isSameHttpMethod(httpMethod);
    }

    public String getPath() {
        RequestURI requestURI = requestLine.getRequestURI();
        return requestURI.getPath();
    }

    public void addHeader(final String key, final String value) {
        headers.addHeader(key, value.trim());
    }

    public boolean hasHeader(final String key) {
        return headers.hasHeader(key);
    }

    public String getHeader(final String key) {
        return headers.getHeader(key);
    }

    public void addBody(final String body) {
        this.body = body;
        if (body.contains("&") && body.contains("=")) {
            this.requestParameters = new RequestParameters(body);
            return;
        }

        this.requestParameters = RequestParameters.EMPTY_PARAMETERS;
    }

    public boolean hasRequestParameter(final String... keys) {
        return requestParameters.hasParameter(keys);
    }

    public String getRequestParameter(final String key) {
        return requestParameters.getParameter(key);
    }
}
