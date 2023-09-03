package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestParameters {

    private final Map<String, String> requestParameters;

    private RequestParameters(final Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public static RequestParameters from(final String queryStrings) {
        final Map<String, String> requestQueryParameters = new HashMap<>();
        if (queryStrings == null || "".equals(queryStrings)){
            return new RequestParameters(requestQueryParameters);
        }
        final String[] queryStringsNameAndValue = queryStrings.split("&");
        for (String queryString : queryStringsNameAndValue) {
            final String[] queryStringNameAndValue = queryString.split("=");
            final String name = queryStringNameAndValue[0];
            final String value = queryStringNameAndValue[1];
            requestQueryParameters.put(name, value);
        }
        return new RequestParameters(requestQueryParameters);
    }

    public String getValue(final String key) {
        return requestParameters.get(key);
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public String toString() {
        return "RequestParameters{" +
                "requestParameters=" + requestParameters +
                '}';
    }
}
