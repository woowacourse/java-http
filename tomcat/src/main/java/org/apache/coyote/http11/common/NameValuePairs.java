package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class NameValuePairs {

    private static final int NAME_IDX = 0;
    private static final int VALUE_IDX = 1;
    private static final int PAIR_ARRAY_LENGTH = 2;

    private final Map<String, String> pairs;

    public NameValuePairs(String nameAndValue, String pairDelimiter, String nameValueDelimiter) {
        this.pairs = parse(nameAndValue, pairDelimiter, nameValueDelimiter);
    }

    private NameValuePairs() {
        this.pairs = new HashMap<>();
    }

    public static NameValuePairs empty() {
        return new NameValuePairs();
    }

    private Map<String, String> parse(String nameAndValue, String pairDelimiter, String nameValueDelimiter) {
        return Arrays.stream(nameAndValue.split(pairDelimiter))
                .map(pair -> pair.split(nameValueDelimiter, PAIR_ARRAY_LENGTH))
                .filter(pair -> pair.length == PAIR_ARRAY_LENGTH)
                .collect(Collectors.toMap(pair -> pair[NAME_IDX], pair -> pair[VALUE_IDX]));
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(pairs.get(name));
    }

    public Map<String, String> getAll() {
        return pairs;
    }
}
