package org.apache.coyote.util;

import org.apache.coyote.http.Constants;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringUtils {

    public static Map<String, String> separate(String term) {
        List<String> bodies = List.of(term.split(Constants.AMPERSAND));
        return bodies.stream()
                .map(s -> s.split(Constants.EQUAL))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    private StringUtils() {

    }
}
