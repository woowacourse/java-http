package org.apache.coyote.http11.request;

public class HttpRequestBody {

    private final String value;
    private final QueryStrings queryStrings;

    private HttpRequestBody(String value, QueryStrings queryStrings) {
        this.value = value;
        this.queryStrings = queryStrings;
    }

    public static HttpRequestBody create(String value) {
        return new HttpRequestBody(value, QueryStrings.from(value));
    }

    public String getValue() {
        return value;
    }

    public String getQueryParam(String param) {
        return queryStrings.getQuery(param);
    }
}
