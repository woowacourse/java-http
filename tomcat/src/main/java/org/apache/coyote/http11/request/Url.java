package org.apache.coyote.http11.request;

import java.net.URI;
import java.util.Map;
import nextstep.jwp.utils.QueryParser;

public class Url {

    private final String path;
    private final Map<String, String> requestParams;

    public Url(String path, Map<String, String> requestParams) {
        this.path = path;
        this.requestParams = requestParams;
    }

    public static Url parseUrl(URI uri) {
        return new Url(uri.getPath(), parseRequestParams(uri.getQuery()));
    }

    private static Map<String, String> parseRequestParams(final String requestParams) {
        if (requestParams == null) {
            return Map.of();
        }

        return QueryParser.parse(requestParams);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }
}
