package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.HttpRequestHeader;

public class Http11RequestHeaderParser {

    public static HttpRequestHeader parse(List<String> lines) {
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
        return new HttpRequestHeader(headers);
    }
}
