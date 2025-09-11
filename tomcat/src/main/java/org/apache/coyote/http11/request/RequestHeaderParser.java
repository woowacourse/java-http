package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaderParser {

    private RequestHeaderParser() {
    }
    
    public static Map<String, String> parse(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            parseHeaderLine(line, headers);
        }
        
        return headers;
    }
    
    private static void parseHeaderLine(String line, Map<String, String> headers) {
        int colonIndex = line.indexOf(':');
        if (colonIndex > 0) {
            String key = line.substring(0, colonIndex).trim().toLowerCase();
            String value = line.substring(colonIndex + 1).trim();
            headers.put(key, value);
        }
    }
}
