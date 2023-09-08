package nextstep.jwp.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.InvalidQueryStringException;

public class RequestUri {
    private final String uri;
    private final Map<String, String> queryParams;

    private RequestUri(final String uri, final Map<String, String> queryParams) {
        this.uri = uri;
        this.queryParams = queryParams;
    }

    public static RequestUri from(final String requestUri) {
        if (notExistQueryParametersIn(requestUri)) {
            final int queryParamStartIndex = requestUri.indexOf("?");
            final String uri = requestUri.substring(0, queryParamStartIndex);
            final Map<String, String> params = parseParams(requestUri.substring(queryParamStartIndex+1));
            return new RequestUri(uri, params);
        }
        return new RequestUri(requestUri, Collections.emptyMap());
    }

    private static boolean notExistQueryParametersIn(final String uri) {
        return !uri.contains("?");
    }

    private static Map<String, String> parseParams(final String params) {
        if(!params.contains("=")) {
            throw new InvalidQueryStringException();
        }
        return Arrays.stream(params.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
