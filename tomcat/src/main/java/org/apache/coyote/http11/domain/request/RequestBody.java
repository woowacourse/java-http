package org.apache.coyote.http11.domain.request;

public class RequestBody {

    private final QueryParameters queryParameters;
    private final String requestBody;

    public RequestBody(String requestBody) {
        this.requestBody = requestBody;
        this.queryParameters = new QueryParameters(requestBody);
    }

    public String getText() {
        return requestBody;
    }

    public String get(String key) {
        return queryParameters.get(key);
    }

    public QueryParameters getQueryParameters() {
        return queryParameters;
    }
}
