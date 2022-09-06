package org.apache.coyote.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import support.StringUtils;

public class Headers {

    public static final String HEADER_DELIMINATOR = ": ";
    private final LinkedList<String> nonKeyValues = new LinkedList<>(); // Map형태로 저장되지 않는 header들

    public Headers(final BufferedReader reader) {
        try {
            Map<String, String> keyValues = new HashMap<>();
            while (reader.ready()) {
                final String headerKeyValue = reader.readLine();
                if (headerKeyValue.contains(HEADER_DELIMINATOR)) {
                    final String[] keyValue = headerKeyValue.split(HEADER_DELIMINATOR);
                    final String key = keyValue[0];
                    final String value = keyValue[1];
                    keyValues.put(key, value);
                } else if (!StringUtils.isEmpty(headerKeyValue)) {
                    System.err.println("Key Value 값 형태가 아님! {" + headerKeyValue + "}");
                    nonKeyValues.add(headerKeyValue);
                }
            }
            System.out.println("headers = {" + keyValues + "}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String findPostContent() {
        return nonKeyValues.stream()
                .filter(nonKeyValue -> !StringUtils.isEmpty(nonKeyValue))
                .findAny()
                .orElse("");
    }
}
