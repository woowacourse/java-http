package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Locale;
import org.apache.coyote.http11.BadRequestException;

public class HttpRequestTarget {

    private static final String ROOT_PATH = "/";
    private static final String ASTERISK = "*";
    private static final String QUERY_DELIMITER = "\\?";
    private static final String SCHEME_DELIMITER = "://";
    private static final List<String> SCHEMES = List.of("http://", "https://");

    private final String authority;
    private final String path;
    private final HttpParams queryParams;

    public HttpRequestTarget(String authority, String path, HttpParams queryParams) {
        this.authority = authority;
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpRequestTarget from(String target) {
        if (isAbsoluteForm(target)) {
            return fromAbsoluteForm(target);
        }
        return fromOriginForm(null, target, target);
    }

    public String getAuthority() {
        return authority;
    }

    public String getPath() {
        return path;
    }

    public String getParams(String key) {
        return queryParams.get(key);
    }

    private static boolean isAbsoluteForm(String target) {
        String lowerCaseTarget = target.toLowerCase(Locale.ROOT);

        return SCHEMES.stream().anyMatch(lowerCaseTarget::startsWith);
    }

    private static HttpRequestTarget fromAbsoluteForm(String target) {
        String withoutScheme = target.substring(target.indexOf(SCHEME_DELIMITER) + SCHEME_DELIMITER.length());
        int authorityEnd = pathStartIndex(withoutScheme);

        String authority = withoutScheme.substring(0, authorityEnd);

        if (authority.isEmpty()) {
            throw new BadRequestException("잘못된 요청 대상입니다: " + target);
        }

        return fromOriginForm(authority, toOriginForm(withoutScheme.substring(authorityEnd)), target);
    }

    private static HttpRequestTarget fromOriginForm(String authority, String originForm, String target) {
        if (!originForm.startsWith(ROOT_PATH) && !ASTERISK.equals(originForm)) {
            throw new BadRequestException("잘못된 요청 대상입니다: " + target);
        }

        String[] targetParts = originForm.split(QUERY_DELIMITER, 2);
        String queryString = extractQueryString(targetParts);

        return new HttpRequestTarget(authority, targetParts[0], HttpParams.from(queryString));
    }

    private static String extractQueryString(String[] targetParts) {
        if (targetParts.length == 2) {
            return targetParts[1];
        }

        return null;
    }

    private static int pathStartIndex(String withoutScheme) {
        return Math.min(indexOrLength(withoutScheme, '/'), indexOrLength(withoutScheme, '?'));
    }

    private static int indexOrLength(String value, char c) {
        int index = value.indexOf(c);

        if (index == -1) {
            return value.length();
        }

        return index;
    }

    private static String toOriginForm(String pathAndQuery) {
        if (pathAndQuery.isEmpty() || pathAndQuery.startsWith("?")) {
            return ROOT_PATH + pathAndQuery;
        }

        return pathAndQuery;
    }
}
