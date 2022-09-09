package http.request;

import http.exception.InvalidHttpRequestFormatException;
import java.net.URI;

public class HttpRequestStartLine {

    private static final int VALID_START_LINE_SIZE = 3;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;

    private final HttpMethod httpMethod;
    private final String url;
    private final QueryParams queryParams;
    private final String protocol;

    public HttpRequestStartLine(final HttpMethod httpMethod, final String url,
                                final QueryParams queryParams, final String protocol) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.queryParams = queryParams;
        this.protocol = protocol;
    }

    public static HttpRequestStartLine parse(final String line) {
        String[] splitStartLine = line.split(" ");
        if (splitStartLine.length != VALID_START_LINE_SIZE) {
            throw new InvalidHttpRequestFormatException();
        }
        String rawMethod = splitStartLine[HTTP_METHOD_INDEX];
        String rawUri = splitStartLine[URL_INDEX];
        URI uri = URI.create(rawUri);
        String url = uri.getPath();
        String queryString = uri.getQuery();
        String protocol = splitStartLine[PROTOCOL_INDEX];
        return new HttpRequestStartLine(HttpMethod.from(rawMethod), url, QueryParams.parse(queryString), protocol);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    public String getProtocol() {
        return protocol;
    }
}
