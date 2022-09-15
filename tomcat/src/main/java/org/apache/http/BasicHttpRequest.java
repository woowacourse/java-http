package org.apache.http;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.info.HttpMethod;

public class BasicHttpRequest implements HttpRequest {

    private static final String QUESTION_MARK = "?";

    private final HttpMethod httpMethod;
    private final String requestUri;
    private final Map<String, String> headers;
    private final Map<String, String> queryParameters;
    private final Map<String, String> body;

    public BasicHttpRequest(final HttpMethod httpMethod, final String requestUri, final Map<String, String> headers,
                            final Map<String, String> queryParameters, final Map<String, String> body) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.headers = headers;
        this.queryParameters = queryParameters;
        this.body = body;
    }

    public static BasicHttpRequest of(final String startLine, final Map<String, String> queryParams,
                                      final Map<String, String> headers, final Map<String, String> body) {
        final var splitStartLine = startLine.split(" ");

        return new BasicHttpRequest(
                HttpMethod.from(splitStartLine[0]),
                splitStartLine[1],
                headers,
                queryParams,
                body
        );
    }

    @Override
    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    @Override
    public String getRequestURI() {
        return this.requestUri;
    }

    @Override
    public String getRequestURIWithoutQueryParams() {
        final var requestURI = getRequestURI();
        if (requestURI.contains(QUESTION_MARK)) {
            return requestURI.substring(0, requestURI.indexOf(QUESTION_MARK));
        }

        return requestURI;
    }

    @Override
    public Object getParameter(final String key) {
        return this.queryParameters.get(key);
    }

    @Override
    public Map<String, String> getParameters() {
        return new HashMap<>(this.queryParameters);
    }

    @Override
    public Map<String, String> getHeaders() {
        return new HashMap<>(this.headers);
    }

    @Override
    public String getHeader(final String headerName) {
        return this.headers.get(headerName);
    }

    @Override
    public Map<String, String> getBody() {
        return this.body;
    }
}
