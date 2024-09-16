package org.apache.coyote.http11.request;

public class HttpRequestBody {

    private final QueryParameters queryParameters;
    private final String requestBody;

    public HttpRequestBody(String requestBody) {
        this.requestBody = requestBody;
        this.queryParameters = new QueryParameters(requestBody);
    }

    public String getRequestBody() {
        return requestBody;
    }

    public QueryParameters getQueryParameters() {
        return queryParameters;
    }
}
