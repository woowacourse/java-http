package nextstep.jwp.http.request;

import java.util.Map;
import nextstep.jwp.http.common.HttpHeaders;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine,
                       final HttpHeaders requestHeaders,
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

    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
