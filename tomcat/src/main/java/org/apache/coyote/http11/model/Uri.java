package org.apache.coyote.http11.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Uri {

    private final String url;
    private final Map<String, String> queryString;

    public Uri(final String uri) {
        url = extractURL(uri);
        queryString = extractQueryString(uri);
    }

    private String extractURL(final String uri) {
        int index = uri.lastIndexOf('?');
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private Map<String, String> extractQueryString(final String uri) {
        int index = uri.lastIndexOf('?');
        if (index == -1) {
            return new HashMap<>();
        }

        final Map<String, String> result = new HashMap<>();
        final String[] queryStrings = uri.substring(index + 1)
                .split("&");

        for (String queryString : queryStrings) {
            String[] keyValuePair = queryString.split("=");
            result.put(keyValuePair[0], keyValuePair[1]);
        }

        return result;
    }

    public ContentType findContentType() {
        final String[] pathInfos = this.url.split("\\.");
        final String fileExtension = pathInfos[pathInfos.length - 1];
        return ContentType.of(fileExtension);
    }

    public FilePath findFilePath() throws IOException {
        return FilePath.of(this.url);
    }
}
