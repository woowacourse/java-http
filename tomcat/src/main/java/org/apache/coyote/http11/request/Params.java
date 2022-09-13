package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import utils.StringSplitter;

public class Params {

    private final Map<String, String> params;

    private Params(final Map<String, String> params) {
        this.params = params;
    }

    public static Params parse(final String query) {
        if (query == null || query.isBlank()) {
            return new Params(Collections.emptyMap());
        }

        final String paramsDelimiter = "&";
        final List<String> params = StringSplitter.split(paramsDelimiter, query);

        final String paramDelimiter = "=";
        return new Params(StringSplitter.getPairs(paramDelimiter, params));
    }

    public static Params empty() {
        return new Params(Collections.emptyMap());
    }

    public Optional<String> find(final String name) {
        return Optional.ofNullable(params.get(name));
    }

    public Map<String, String> getParams() {
        return params;
    }
}
