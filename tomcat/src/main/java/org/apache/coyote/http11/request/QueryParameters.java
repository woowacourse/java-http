package org.apache.coyote.http11.request;

import org.apache.coyote.http11.PercentDecoder;
import org.apache.coyote.http11.exception.BadRequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryParameters {
    private static final String PARAMETER_DELIMITER = "&";
    private static final String NAME_VALUE_DELIMITER = "=";
    private static final String EMPTY_VALUE = "";
    private static final int NOT_FOUND = -1;
    private static final int MAX_PARAMETER_COUNT = 1_000;

    private static final QueryParameters EMPTY = new QueryParameters(Map.of());

    private final Map<String, List<String>> values;

    private QueryParameters(final Map<String, List<String>> values) {
        this.values = values;
    }

    public static QueryParameters empty() {
        return EMPTY;
    }
    public static QueryParameters from(final String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return EMPTY;
        }
        return new QueryParameters(parse(queryString));
    }

    private static Map<String, List<String>> parse(final String queryString) {
        final String[] pairs = queryString.split(PARAMETER_DELIMITER, MAX_PARAMETER_COUNT + 1);
        if (pairs.length > MAX_PARAMETER_COUNT) {
            throw new BadRequestException("파라미터 개수가 너무 많습니다");
        }

        final Map<String, List<String>> parameters = new LinkedHashMap<>();
        for (final String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }
            final int delimiterIndex = pair.indexOf(NAME_VALUE_DELIMITER);
            final String rawName = delimiterIndex == NOT_FOUND ? pair : pair.substring(0, delimiterIndex);
            final String rawValue = delimiterIndex == NOT_FOUND ? EMPTY_VALUE : pair.substring(delimiterIndex + 1);

            final String name = PercentDecoder.decodeForm(rawName);
            if (name.isBlank()) {
                continue;
            }
            parameters.computeIfAbsent(name, key -> new ArrayList<>())
                    .add(PercentDecoder.decodeForm(rawValue));
        }
        return toImmutable(parameters);
    }

    private static Map<String, List<String>> toImmutable(final Map<String, List<String>> source) {
        return source.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue())
                ));
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Optional<String> get(final String name) {
        return getAll(name).stream().findFirst();
    }

    public List<String> getAll(final String name) {
        if (name == null) {
            return List.of();
        }
        return values.getOrDefault(name, List.of());
    }

    @Override
    public String toString() {
        return "QueryParameters" + values;
    }
}
