package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeaderParser {
    public static Map<String, String> parseHeader(BufferedReader reader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            String[] parts = line.split(":");
            String key = parts[0].trim();
            String value = parts[1].trim();

            header.put(key, value);
        }
        return header;
    }
}
