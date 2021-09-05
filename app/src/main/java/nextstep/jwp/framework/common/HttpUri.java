package nextstep.jwp.framework.common;

import nextstep.jwp.framework.file.FileExtension;
import nextstep.jwp.utils.StringUtils;

import java.util.Map;
import java.util.Objects;

public class HttpUri {

    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final String QUERY_STRING_PIECE_SEPARATOR = "&";
    private static final String QUERY_STRING_PARAM_SEPARATOR = "=";

    private final String value;

    public HttpUri(String value) {
        this.value = value;
    }

    public boolean isStaticFilePath() {
        String path = removedQueryStringPath();
        return FileExtension.supports(path);
    }

    public String removedQueryStringPath() {
        if (notExistsQueryString()) {
            return value;
        }
        int index = value.indexOf(QUERY_STRING_SEPARATOR);
        return value.substring(0, index);
    }

    private boolean notExistsQueryString() {
        return !value.contains(QUERY_STRING_SEPARATOR);
    }

    public String extractQueryString() {
        if (notExistsQueryString()) {
            throw new IllegalStateException("QueryString 이 존재하지 않습니다.");
        }
        int index = value.indexOf(QUERY_STRING_SEPARATOR);
        return value.substring(index + 1);
    }

    public Map<String, String> extractQueryParams() {
        String queryString = extractQueryString();
        return StringUtils.extractMap(queryString, QUERY_STRING_PIECE_SEPARATOR, QUERY_STRING_PARAM_SEPARATOR);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpUri httpUri = (HttpUri) o;
        return Objects.equals(getValue(), httpUri.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
