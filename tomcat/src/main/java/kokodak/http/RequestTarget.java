package kokodak.http;

import static kokodak.Constants.BLANK;

import java.util.HashMap;
import java.util.Map;

public class RequestTarget {

    private String requestTarget;

    private String path;

    private Map<String, String> queryString;

    private RequestTarget(final String requestTarget, final String path, final Map<String, String> queryString) {
        this.requestTarget = requestTarget;
        this.path = path;
        this.queryString = queryString;
    }

    public static RequestTarget from(final String startLine) {
        final String requestTarget = getRequestTarget(startLine);
        final String path = getPath(requestTarget);
        final Map<String, String> queryString = getQueryString(requestTarget);
        return new RequestTarget(requestTarget, path, queryString);
    }

    private static String getRequestTarget(final String startLine) {
        return startLine.split(BLANK.getValue())[1];
    }

    private static String getPath(final String requestTarget) {
        return requestTarget.split("\\?")[0];
    }

    private static Map<String, String> getQueryString(final String requestTarget) {
        final Map<String, String> queryString = new HashMap<>();
        final String[] split = requestTarget.split("\\?");
        if (split.length > 1) {
            for (final String query : split[1].split("&")) {
                final String[] keyValue = query.split("=");
                queryString.put(keyValue[0], keyValue[1]);
            }
        }
        return queryString;
    }

    public boolean hasQueryString() {
        return !queryString.isEmpty();
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getPath() {
        return path;
    }

    public String queryString(final String key) {
        return queryString.get(key);
    }
}
