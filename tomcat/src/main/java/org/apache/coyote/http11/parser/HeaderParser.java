package org.apache.coyote.http11.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeaderParser {

    public static final String HEADER_DELIMITER = ":";
    public static final String MEDIA_TYPE_DELIMITER = ",";
    public static final String ACCEPT_HEADER = "Accept";

    public static Map<String, String> parse(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] split = line.split(HEADER_DELIMITER, 2);
            headers.put(split[0], split[1]);
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
