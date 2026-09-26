package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.FormUrlEncoded;

public class RequestParams {

    private final Map<String, List<String>> values;

    public RequestParams(final String queryString) {
        this.values = FormUrlEncoded.parse(queryString);
    }

    private RequestParams(final Map<String, List<String>> values) {
        final Map<String, List<String>> copied = new HashMap<>();
        values.forEach((name, entries) -> copied.put(name, List.copyOf(entries)));
        this.values = Map.copyOf(copied);
    }

    public RequestParams append(final RequestParams additional) {
        final Map<String, List<String>> merged = new HashMap<>();
        values.forEach((name, entries) -> merged.put(name, new ArrayList<>(entries)));
        additional.values.forEach((name, entries) ->
                merged.computeIfAbsent(name, key -> new ArrayList<>()).addAll(entries));
        return new RequestParams(merged);
    }

    public String get(final String name) {
        final List<String> entries = getValues(name);
        if (entries.isEmpty()) {
            return null;
        }
        return entries.getFirst();
    }

    public List<String> getValues(final String name) {
        return values.getOrDefault(name, List.of());
    }
}
