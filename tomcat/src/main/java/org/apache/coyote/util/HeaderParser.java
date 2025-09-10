package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.cookie.HttpCookie;

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

    public static Map<String, String> createRedirectHeaders(String location, HttpCookie cookie, String sessionId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", location);
        if (cookie != null && sessionId != null) {
            headers.put("Set-Cookie", cookie.createSessionCookie(sessionId));
        }
        return headers;
    }

}
