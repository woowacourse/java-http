package org.apache.coyote.http11.http.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.http.common.HttpSplitFormat;

public class HttpQueryParameter {

    private final Map<String, String> queryParameterInfo;

    private HttpQueryParameter(final Map<String, String> queryParameterInfo) {
        this.queryParameterInfo = queryParameterInfo;
    }

    public static HttpQueryParameter from(final String path) {
        final String targetPath = path.trim();
        final int queryParameterStartIndex = targetPath.indexOf(HttpSplitFormat.QUERY_PARAMETER_START.getValue());
        if (queryParameterStartIndex != -1 && queryParameterStartIndex != targetPath.length() - 1) {

            final String queryParameterLine = targetPath.substring(queryParameterStartIndex + 1);
            final String[] queryParameterElements = queryParameterLine.split(
                    HttpSplitFormat.QUERY_PARAMETER.getValue());

            return new HttpQueryParameter(createQueryParameterInfo(queryParameterElements));
        }
        return new HttpQueryParameter(new HashMap<>());
    }

    private static Map<String, String> createQueryParameterInfo(final String[] queryParameterElements) {
        final Map<String, String> queryParameterInfo = new HashMap<>();
        for (final String queryParameterElement : queryParameterElements) {
            String queryParameterElementLine = queryParameterElement.trim();
            parseQueryParameterElement(queryParameterElementLine, queryParameterInfo);
        }
        return queryParameterInfo;
    }

    private static void parseQueryParameterElement(final String queryParameterElementLine,
                                                   final Map<String, String> queryParameterInfo) {
        int queryParameterSplitIndex = queryParameterElementLine
                .indexOf(HttpSplitFormat.QUERY_PARAMETER_ELEMENT.getValue());
        validateQueryParameterElementFormat(queryParameterSplitIndex);

        final String queryParameterElementKey = queryParameterElementLine
                .substring(0, queryParameterSplitIndex)
                .trim();
        final String queryParameterElementValue = queryParameterElementLine
                .substring(queryParameterSplitIndex + 1)
                .trim();
        validateQueryParameterKeyFormat(queryParameterElementKey);
        validateQueryParameterValueFormat(queryParameterElementValue);
        queryParameterInfo.put(queryParameterElementKey, queryParameterElementValue);
    }

    private static void validateQueryParameterKeyFormat(final String queryParameterElementKey) {
        if (queryParameterElementKey == null) {
            throw new IllegalArgumentException("queryParameter key는 null일 수 없습니다.");
        }
        if (queryParameterElementKey.isBlank()) {
            throw new IllegalArgumentException("query parameter key는 빈 값일 수 없습니다");
        }
    }

    private static void validateQueryParameterValueFormat(final String queryParameterElementValue) {
        if (queryParameterElementValue == null) {
            throw new IllegalArgumentException("queryParameter value는 null일 수 없습니다.");
        }
    }

    private static void validateQueryParameterElementFormat(final int queryParameterSplitIndex) {
        if (queryParameterSplitIndex == -1) {
            throw new IllegalArgumentException("유효하지 않은 query parameter element format 입니다.");
        }
    }

    public String getValue(final String target) {
        if (target == null) {
            throw new IllegalArgumentException("query parameter key는 null일 수 없습니다");
        }

        final String targetKey = target.trim();

        if (!queryParameterInfo.containsKey(targetKey)) {
            throw new IllegalArgumentException("존재하지 않는 query parameter key 입니다: %s".formatted(target));
        }
        return queryParameterInfo.get(targetKey);
    }
}
