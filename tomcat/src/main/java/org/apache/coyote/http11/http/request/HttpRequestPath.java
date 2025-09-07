package org.apache.coyote.http11.http.request;

import org.apache.coyote.http11.http.common.HttpSplitFormat;

public class HttpRequestPath {

    private final String rootPath;
    private final HttpQueryParameter queryParameter;

    private HttpRequestPath(final String rootPath, final HttpQueryParameter queryParameter) {
        this.rootPath = rootPath;
        this.queryParameter = queryParameter;
    }

    public static HttpRequestPath from(final String path) {
        validateNull(path);
        final String rootPath = removeQueryParameterLine(path);
        final HttpQueryParameter queryParameter = HttpQueryParameter.from(path);
        return new HttpRequestPath(rootPath, queryParameter);
    }

    private static void validateNull(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("path는 null일 수 없습니다");
        }
    }

    private static String removeQueryParameterLine(final String path) {
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

    public String getRootPath() {
        return rootPath;
    }

    public String getTargetQueryParameter(final String target) {
        validateNullTargetQueryParameter(target);
        final String targetQueryParameterKey = target.trim();
        return queryParameter.getValue(targetQueryParameterKey);
    }
}
