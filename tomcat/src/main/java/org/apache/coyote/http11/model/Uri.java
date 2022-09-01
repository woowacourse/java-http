package org.apache.coyote.http11.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Uri {

    private static final char QUERY_STRING_STANDARD = '?';
    private static final String QUERY_STRINGS_BOUNDARY = "&";
    private static final String KEY_VALUE_BOUNDARY = "=";
    private static final String PATH_REGEX = "\\.";
    private static final int INDEX_NOT_FOUND = -1;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String url;
    private final Map<String, String> queryString;

    public Uri(final String uri) {
        url = extractURL(uri);
        queryString = extractQueryString(uri);
    }

    private String extractURL(final String uri) {
        int index = uri.lastIndexOf(QUERY_STRING_STANDARD);
        if (index == INDEX_NOT_FOUND) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private Map<String, String> extractQueryString(final String uri) {
        int index = uri.lastIndexOf(QUERY_STRING_STANDARD);
        if (index == INDEX_NOT_FOUND) {
            return new HashMap<>();
        }

        final Map<String, String> result = new HashMap<>();
        final String[] queryStrings = uri.substring(index + 1)
                .split(QUERY_STRINGS_BOUNDARY);

        for (String queryString : queryStrings) {
            String[] keyValuePair = queryString.split(KEY_VALUE_BOUNDARY);
            result.put(keyValuePair[KEY_INDEX], keyValuePair[VALUE_INDEX]);
        }

        return result;
    }

    public ContentType findContentType() {
        final String[] pathInfos = this.url.split(PATH_REGEX);
        final String fileExtension = pathInfos[pathInfos.length - 1];
        return ContentType.of(fileExtension);
    }

    public FilePath findFilePath() throws IOException {
        return FilePath.of(this.url);
    }
}
