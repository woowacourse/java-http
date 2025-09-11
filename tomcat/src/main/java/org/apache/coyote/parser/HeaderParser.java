package org.apache.coyote.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeaderParser {

    private static final String HEADER_DELIMITER = ":";
    private static final String MEDIA_TYPE_DELIMITER = ",";
    private static final String ACCEPT_HEADER = "Accept";

    public static Map<String, String> parse(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] split = line.split(HEADER_DELIMITER, 2);
            String key = split[0].trim();
            String value = split[1].trim();
            headers.put(key, value);
        }
        return headers;
    }

    public static String extractPrimaryMimeType(Map<String, String> headers) {
        if (!headers.containsKey(ACCEPT_HEADER)) {
            return "";
        }
        String acceptHeaderValue = headers.get(ACCEPT_HEADER);
        return acceptHeaderValue.split(MEDIA_TYPE_DELIMITER)[0];
    }
}
