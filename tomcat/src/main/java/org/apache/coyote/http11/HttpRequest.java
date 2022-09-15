package org.apache.coyote.http11;

import org.apache.catalina.session.Session;

public class HttpRequest {

    private static final String SPACE_DELIMITER = " ";
    private static final String QUERY_STRING_START = "?";

    private HttpMethod httpMethod;
    private String url;
    private QueryStrings queryStrings;
    private final HttpHeaders httpHeaders;
    private final Session session;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(final HttpReader httpReader, final Session session) {
        parseStartLine(httpReader.getStartLine());
        this.httpHeaders = httpReader.getHttpHeaders();
        this.session = session;
        this.httpRequestBody = new HttpRequestBody(httpReader.getBody());
    }

    private void parseStartLine(final String startLine) {
        final String[] values = startLine.split(SPACE_DELIMITER);
        this.httpMethod = HttpMethod.of(values[0]);
        this.url = parseUri(values[1]);
    }

    private String parseUri(final String uri) {
        if (uri.contains(QUERY_STRING_START)) {
            final int index = uri.indexOf(QUERY_STRING_START);
            this.queryStrings = new QueryStrings(uri.substring(index + 1));
            return uri.substring(0, index);
        }
        return uri;
    }

    public String getQueryStringValue(final String parameter) {
        if (queryStrings == null) {
            return "";
        }
        return queryStrings.getValue(parameter);
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Session getSession() {
        return session;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }
}
