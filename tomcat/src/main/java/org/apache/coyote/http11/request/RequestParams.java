package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RequestParams {

    private final Map<String, String> requestParams;

    public RequestParams(final Map<String, String> requestParams) {
        this.requestParams = requestParams;
    }

    public static RequestParams from(final QueryParams queryParams) {
        return new RequestParams(queryParams.getContents());
    }

    public static RequestParams fromUrlEncoded(final String urlEncodedQueryString) {
        final String queryString = URLDecoder.decode(urlEncodedQueryString, StandardCharsets.UTF_8);
        return from(QueryParams.from(queryString));
    }

    public static RequestParams empty() {
        return new RequestParams(new HashMap<>());
    }

    public Optional<String> getValue(final String key) {
        return Optional.ofNullable(requestParams.get(key));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestParams)) {
            return false;
        }
        final RequestParams that = (RequestParams) o;
        return Objects.equals(requestParams, that.requestParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestParams);
    }
}
