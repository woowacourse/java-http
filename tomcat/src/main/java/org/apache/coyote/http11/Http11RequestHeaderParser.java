package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11RequestHeaderParser {

    public static Http11RequestHeader parse(List<String> lines) {
        Map<String, String> headers = new HashMap<>();
        for (String line : lines) {
            int index = line.indexOf(":");
            if (index == -1) {
                break;
            }
            String key = line.substring(0, index).trim();
            String value = line.substring(index + 1).trim();
            headers.put(key, value);
        }
        return new Http11RequestHeader(headers);
    }
}
