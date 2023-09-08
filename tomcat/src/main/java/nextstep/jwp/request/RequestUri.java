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
        final int index = requestUri.indexOf("?");
        if (index != -1) {
            String uri = requestUri.substring(0, index);
            Map<String, String> params = parseParams(requestUri.substring(index+1));
            return new RequestUri(uri, params);
        }
        return new RequestUri(requestUri, Collections.emptyMap());
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
