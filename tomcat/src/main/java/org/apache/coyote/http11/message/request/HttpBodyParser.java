package org.apache.coyote.http11.message.request;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpBodyParser {

    private static final String ENTRIES_DELIMITER = "&";
    private static final String ENTRY_DELIMITER = "=";
    private static final int ENTRY_KEY_INDEX = 0;
    private static final int ENTRY_VALUE_INDEX = 1;

    private HttpBodyParser() {
    }

    public static FormParameters parseToFormParameters(byte[] body, HttpHeaders headers) {
        if (body.length == 0 || !headers.isHeader("Content-Type", "application/x-www-form-urlencoded")) {
            return new FormParameters(new HashMap<>());
        }

        String bodyString = new String(body);

        return new FormParameters(Arrays.stream(bodyString.split(ENTRIES_DELIMITER))
                .map(HttpBodyParser::parseEntry)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, HttpBodyParser::mergeValues)));
    }

    private static Map.Entry<String, List<String>> parseEntry(String entry) {
        String[] queryParameterElements = entry.split(ENTRY_DELIMITER);
        String key = URLDecoder.decode(queryParameterElements[ENTRY_KEY_INDEX], Charset.defaultCharset());
        String value = URLDecoder.decode(queryParameterElements[ENTRY_VALUE_INDEX], Charset.defaultCharset());

        return Map.entry(key, List.of(value));
    }

    private static List<String> mergeValues(List<String> existingValues, List<String> newValues) {
        existingValues.addAll(newValues);
        return existingValues;
    }
}
