package org.apache.coyote.http11.http.request;

import org.apache.coyote.http11.http.common.HttpSplitFormat;

public class HttpRequestPath {

    private final String path;
    private final HttpQueryParameter queryParameter;

    private HttpRequestPath(final String path) {
        this.path = removeQueryParameterLine(path);
        this.queryParameter = HttpQueryParameter.from(path);
    }

    public static HttpRequestPath from(final String path) {
        validateNull(path);
        return new HttpRequestPath(path);
    }

    private static void validateNull(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("path는 null일 수 없습니다");
        }
    }

    private String removeQueryParameterLine(final String path) {
        int queryParameterStartIndex = path.indexOf(HttpSplitFormat.QUERY_PARAMETER_START.getValue());
        if (queryParameterStartIndex == -1) {
            return path;
        }
        return path.substring(0, queryParameterStartIndex);
    }

    private void validateNullTargetQueryParameter(final String target) {
        if (target == null) {
            throw new IllegalArgumentException("query parameter key는 null일 수 없습니다");
        }
    }

    public String getPath() {
        return path;
    }

    public String getTargetQueryParameter(final String target) {
        validateNullTargetQueryParameter(target);
        final String targetQueryParameterKey = target.trim();
        return queryParameter.getValue(targetQueryParameterKey);
    }
}
