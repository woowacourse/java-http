package org.apache.coyote.support;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ContentType;

public class HttpRequestParser {

    private static final String EXTENSION_DELIMITER = ".";
    public static final int HEADER_END_NOT_FOUND = -1;

    private HttpRequestParser() {
    }

    public static String parseBody(final List<String> httpRequestLines) {
        int headerEndIndex = findHeaderEnd(httpRequestLines);
        return httpRequestLines.subList(headerEndIndex + 1, httpRequestLines.size())
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static int findHeaderEnd(final List<String> httpRequestLines) {
        int index = httpRequestLines.indexOf("");
        if (index == HEADER_END_NOT_FOUND) {
            return httpRequestLines.size();
        }
        return index;
    }

    public static ContentType parseContentType(final String uri) {
        String extension = parseExtension(uri);
        return ContentType.fromExtension(extension);
    }

    public static String parseExtension(final String url) {
        if (url.contains(EXTENSION_DELIMITER)) {
            return url.substring(url.lastIndexOf(EXTENSION_DELIMITER) + 1);
        }
        return "";
    }
}
