package org.apache.coyote.http11.httprequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers;

    public Headers(List<String> headers) {
        this.headers = toMap(headers);
    }

    private Map<String, String> toMap(List<String> lines) {
        Map<String, String> headers = new LinkedHashMap<>();

        for (String line : lines) {
            int index = line.indexOf(HEADER_DELIMITER);
            headers.put(line.substring(0, index), line.substring(index + 2));
        }

        return headers;
    }

    public int getContentLength() {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }
}
