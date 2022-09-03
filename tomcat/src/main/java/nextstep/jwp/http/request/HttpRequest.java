package nextstep.jwp.http.request;

import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine,
                       final RequestHeaders requestHeaders,
                       final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public String getRequestMethod() {
        return requestLine.getRequestMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public String getRequestExtension() {
        return requestLine.getRequestExtension();
    }

    public String getQueryParameterValue(final String parameter) {
        return requestLine.getQueryParameterValue(parameter);
    }

    public Map<String, String> getQueryParameters() {
        return requestLine.getQueryParameters();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
