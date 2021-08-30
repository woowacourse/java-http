package nextstep.jwp.model;

import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    private String method;
    private Uri uri;
    private Map<String, String> httpHeaders;
    private RequestBody requestBody;

    private HttpRequest(String method, Uri uri, Map<String, String> httpHeaders, RequestBody requestBody) {
        this.method = method;
        this.uri = uri;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(RequestLine requestLine, Map<String, String> httpHeaders) {
        return new HttpRequest(requestLine.getMethod(), requestLine.getUri(), httpHeaders, null);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri.getUri();
    }

    public Map<String, String> getQueryMap() {
        return uri.getQueryMap();
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public String getHttpHeader(String headerValue) {
        if (Objects.isNull(httpHeaders.get(headerValue))) {
            throw new IllegalArgumentException("해당 헤더 값이 존재하지 않습니다.");
        }
        return httpHeaders.get(headerValue);
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
