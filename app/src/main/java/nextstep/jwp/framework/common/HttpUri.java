package nextstep.jwp.framework.common;

import nextstep.jwp.framework.file.FileExtension;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
            throw new IllegalStateException("QueryString이 존재하지 않습니다.");
        }
        int index = value.indexOf(QUERY_STRING_SEPARATOR);
        return value.substring(index + 1);
    }

    public Map<String, String> extractQueryParams() {
        String queryString = extractQueryString();
        String[] pieces = queryString.split(QUERY_STRING_PIECE_SEPARATOR);

        return Arrays.stream(pieces)
                .filter(this::isValidQueryStringPiece)
                .map(this::extractParam)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean isValidQueryStringPiece(String queryStringPiece) {
        String[] keyAndValue = queryStringPiece.split(QUERY_STRING_PARAM_SEPARATOR);
        return keyAndValue.length == 2;
    }

    private Map.Entry<String, String> extractParam(String queryStringPiece) {
        int index = queryStringPiece.indexOf(QUERY_STRING_PARAM_SEPARATOR);
        String k = queryStringPiece.substring(0, index);
        String v = queryStringPiece.substring(index + 1);
        return new AbstractMap.SimpleEntry<>(k, v);
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
