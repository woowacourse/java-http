package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestParameters {

    private final Map<String, String> requestParameters;

    private RequestParameters(final Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public static RequestParameters from(final String requestFirstLine) {
        final String[] requestFirstLineElements = requestFirstLine.split(" ");
        final String requestUriValue = requestFirstLineElements[1];

        final Map<String, String> requestParameters = getRequestParameters(requestUriValue);

        return new RequestParameters(requestParameters);
    }

    private static Map<String, String> getRequestParameters(final String requestUriValue) {
        final Map<String, String> requestQueryParameters = new HashMap<>();
        if (!requestUriValue.contains("?")) {
            return requestQueryParameters;
        }
        final String queryStrings = requestUriValue.substring(requestUriValue.indexOf("?") + 1);
        final String[] queryStringsNameAndValue = queryStrings.split("&");
        for (String queryString : queryStringsNameAndValue) {
            final String[] queryStringNameAndValue = queryString.split("=");
            final String name = queryStringNameAndValue[0];
            final String value = queryStringNameAndValue[1];
            requestQueryParameters.put(name, value);
        }
        return requestQueryParameters;
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
