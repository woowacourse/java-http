package org.apache.coyote.http11.message;

import java.util.List;
import java.util.Map;

public class HttpHeadersStringifier {

    private HttpHeadersStringifier() {
    }

    private static final String HEADER_FIELDS_DELIMITER = "; ";
    private static final String HEADER_LINE_DELIMITER = ": ";

    public static List<String> stringify(HttpHeaders headers) {
        return headers.getHeaders()
                .entrySet()
                .stream()
                .map(HttpHeadersStringifier::toHeaderLine)
                .toList();
    }

    private static String toHeaderLine(Map.Entry<String, List<String>> headersEntry) {
        String fields = String.join(HEADER_FIELDS_DELIMITER, headersEntry.getValue());
        return String.join(HEADER_LINE_DELIMITER, headersEntry.getKey(), fields);
    }
}
