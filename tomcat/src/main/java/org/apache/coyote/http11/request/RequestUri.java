package org.apache.coyote.http11.request;

public class RequestUri {

    private static final String QUERY_PARAMETER_DELIMITER = "?";
    private static final String QUERY_PARAMETER_REGEX = "\\?";

    private final String uri;
    private final QueryParameter queryParameter;

    public RequestUri(String uri) {
        this.uri = uri;
        this.queryParameter = new QueryParameter(uri);
    }

    public boolean hasQueryParameter() {
        return !queryParameter.isEmpty();
    }

    public String getQueryParameterAttribute(String name) {
        return queryParameter.getAttribute(name);
    }

    public String getPath() {
        if (uri.contains(QUERY_PARAMETER_DELIMITER)) {
            return uri.split(QUERY_PARAMETER_REGEX)[0];
        }
        return uri;
    }

    public String getUri() {
        return uri;
    }
}
