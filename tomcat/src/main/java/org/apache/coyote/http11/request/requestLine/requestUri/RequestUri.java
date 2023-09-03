package org.apache.coyote.http11.request.requestLine.requestUri;

import java.util.Objects;
import java.util.StringTokenizer;

public class RequestUri {

    private static final String QUERY_STRING_BEGIN_SIGN = "?";

    private final ResourcePath resourcePath;
    private final QueryParameters queryParameters;

    private RequestUri(final ResourcePath resourcePath, final QueryParameters queryParameters) {
        this.resourcePath = resourcePath;
        this.queryParameters = queryParameters;
    }

    public static RequestUri from(final String uri) {
        StringTokenizer stringTokenizer = new StringTokenizer(uri, QUERY_STRING_BEGIN_SIGN);

        final String resourcePath = stringTokenizer.nextToken();
        String queryString = "";
        if (stringTokenizer.hasMoreTokens()) {
            queryString = stringTokenizer.nextToken();
        }

        return new RequestUri(
                ResourcePath.from(resourcePath),
                QueryParameters.from(queryString)
        );
    }

    public boolean isQueryParameterEmpty() {
        return queryParameters.isEmpty();
    }

    public ResourcePath getResourcePath() {
        return resourcePath;
    }

    public QueryParameters getQueryParameters() {
        return queryParameters;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestUri that = (RequestUri) o;
        return Objects.equals(resourcePath, that.resourcePath) && Objects.equals(queryParameters, that.queryParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourcePath, queryParameters);
    }

    @Override
    public String toString() {
        return "RequestUri{" +
                "resourcePath=" + resourcePath +
                ", queryParameters=" + queryParameters +
                '}';
    }
}
