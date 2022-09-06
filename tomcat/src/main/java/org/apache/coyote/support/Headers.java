package org.apache.coyote.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import support.StringUtils;

public class Headers {

    public static final String HEADER_DELIMINATOR = ": ";
    private final Map<String, String> keyValues = new HashMap<>();

    public Headers(final BufferedReader reader) {
        try {
            while (reader.ready()) {
                final String headerKeyValue = reader.readLine();
                if (headerKeyValue.contains(HEADER_DELIMINATOR)) {
                    final String[] keyValue = headerKeyValue.split(HEADER_DELIMINATOR);
                    final String key = keyValue[0];
                    final String value = keyValue[1];
                    keyValues.put(key, value);
                } else if (StringUtils.isEmpty(headerKeyValue)) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(final String key) {
        return keyValues.get(key);
    }
}
