package org.apache.coyote.http11;

/**
 * 요청의 첫 줄을 파싱해 메서드, 경로, 쿼리 파라미터, HTTP 버전으로 나눠 보관함.
 */
public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final RequestParameters queryParameters;
    private final String httpVersion;

    private RequestLine(HttpMethod method, String path, RequestParameters queryParameters, String httpVersion) {
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        String method = requestLineParts[0];
        String requestUri = requestLineParts[1];
        String httpVersion = requestLineParts[2];

        String[] uriParts = requestUri.split("\\?");
        String path = uriParts[0];
        String queryString = "";
        if (uriParts.length > 1) {
            queryString = uriParts[1];
        }

        return new RequestLine(HttpMethod.valueOf(method), path, RequestParameters.from(queryString), httpVersion);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public RequestParameters getQueryParameters() {
        return queryParameters;
    }

    public boolean isGet() {
        return method == HttpMethod.GET;
    }

    public boolean isPost() {
        return method == HttpMethod.POST;
    }
}
