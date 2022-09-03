package org.apache.coyote.support;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.ContentType;
import org.apache.coyote.exception.InvalidHttpRequestFormatException;

public class UriParser {

    private static final int URL_INDEX = 1;
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String EXTENSION_DELIMITER = ".";
    public static final int VALID_START_LINE_SIZE = 3;
    public static final int DELIMITER_NOT_FOUND = -1;

    private UriParser() {
    }

    public static String parseUri(final BufferedReader bufferedReader) throws IOException {
        String[] firstLineElements = bufferedReader.readLine().split(" ");
        if (firstLineElements.length != VALID_START_LINE_SIZE) {
            throw new InvalidHttpRequestFormatException();
        }
        return firstLineElements[URL_INDEX];
    }

    public static String parseUrl(final String uri) {
        int lastIndexOfQueryStringDelimiter = uri.lastIndexOf(QUERY_STRING_DELIMITER);
        if (lastIndexOfQueryStringDelimiter == DELIMITER_NOT_FOUND) {
            return uri;
        }
        return uri.substring(0, lastIndexOfQueryStringDelimiter);
    }

    public static String parseQueryString(final String uri) {
        int lastIndexOfQueryStringDelimiter = uri.lastIndexOf(QUERY_STRING_DELIMITER);
        if (lastIndexOfQueryStringDelimiter == DELIMITER_NOT_FOUND) {
            return "";
        }
        return uri.substring(lastIndexOfQueryStringDelimiter + 1);
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
