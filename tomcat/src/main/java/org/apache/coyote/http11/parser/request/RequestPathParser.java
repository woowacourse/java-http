package org.apache.coyote.http11.parser.request;

import static org.apache.coyote.http11.HttpConstants.QUESTION;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.dto.request.RequestPath;

public final class RequestPathParser {

    public static RequestPath parse(final String path) {
        final String uri = parseUri(path);
        final Map<String, String> queryParams = parseQueryParams(path);
        return new RequestPath(uri, queryParams);
    }

    public static String parseUri(final String path) {
        final int qIdx = path.indexOf(QUESTION);
        return (qIdx >= 0) ? path.substring(0, qIdx) : path;
    }

    public static Map<String, String> parseQueryParams(final String path) {
        final int qIdx = path.indexOf(QUESTION);
        if (qIdx < 0) {
            return new HashMap<>();
        }
        final String query = path.substring(qIdx + 1);
        return QueryParser.parse(query);
    }
}
