package org.apache.coyote.domain.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.domain.HttpCookie;

public class HttpRequest {

    private static final String HEADER_DELIMITER = " ";

    private final String uri;
    private final HttpMethod httpMethod;
    private final QueryParam queryParam;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;
    private final HttpCookie httpCookie;

    private HttpRequest(HttpMethod httpMethod, String uri, QueryParam queryParam, RequestHeader requestHeader,
                        RequestBody requestBody, HttpCookie httpCookie) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.queryParam = queryParam;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.httpCookie = httpCookie;
    }

    public static HttpRequest from(BufferedReader inputReader) {
        try {
            String startLine = inputReader.readLine();
            String[] headers = startLine.split(HEADER_DELIMITER);
            HttpMethod httpMethod = HttpMethod.get(headers[0]);
            RequestHeader requestHeader = RequestHeader.from(inputReader);
            HttpCookie httpCookie = HttpCookie.from(requestHeader.getCookies());
            RequestBody requestBody = RequestBody.of(inputReader, requestHeader.getContentLength());
            return new HttpRequest(httpMethod, headers[1], QueryParam.from(headers[1]), requestHeader, requestBody,
                    httpCookie);
        } catch (IOException e) {
            throw new IllegalArgumentException("");
        }
    }

    public String getUri() {
        return uri;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
