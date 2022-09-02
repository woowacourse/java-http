package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.ContentType;
import org.apache.coyote.http11.enums.FilePath;

public class HttpRequest {

    private static final char QUERY_STRING_STANDARD = '?';
    private static final int INDEX_NOT_FOUND = -1;

    private final String url;
    private final QueryStrings queryStrings;

    public HttpRequest(final String uri) {
        url = extractURL(uri);
        queryStrings = new QueryStrings(uri);
    }

    private String extractURL(final String uri) {
        int index = uri.lastIndexOf(QUERY_STRING_STANDARD);
        if (index == INDEX_NOT_FOUND) {
            return uri;
        }
        return uri.substring(0, index);
    }

    public boolean isFindFile() {
        return findFilePath().isFilePath(url);
    }

    public ContentType findContentType() {
        return ContentType.of(
                findFilePath().findFileExtension()
        );
    }

    public FilePath findFilePath() {
        return FilePath.of(url);
    }

    public QueryStrings getQueryStrings() {
        return queryStrings;
    }
}
