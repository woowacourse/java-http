package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {
    private final Map<String, String> parameters;

    private QueryString(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static QueryString of(final List<String> requestTokens) {
        if (requestTokens.size() == 1) {
            return new QueryString(Map.of());
        }
        final String queryString = requestTokens.get(1);
        final String[] tokens = queryString.split("&");
        final Map<String, String> parameters = Arrays.stream(tokens)
                .map(token -> token.split("="))
                .collect(Collectors.toMap(token -> token[0], token -> token[1]));
        return new QueryString(parameters);
    }

    public String getValue(final String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        }
        throw new IllegalArgumentException("존재하지 않는 파라미터입니다.");
    }
}
