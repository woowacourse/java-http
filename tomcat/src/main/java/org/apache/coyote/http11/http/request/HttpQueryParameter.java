package org.apache.coyote.http11.http.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.http.common.HttpSplitFormat;

public class HttpQueryParameter {

    private final Map<String, String> queryParameterInfo;

    public static HttpQueryParameter from(final String path) {
        return new HttpQueryParameter(path);
    }

    private HttpQueryParameter(final String path) {
        String targetPath = clean(path);
        int queryParameterStartIndex = targetPath.indexOf(HttpSplitFormat.QUERY_PARAMETER_START.getValue());
        if (queryParameterStartIndex != -1 && queryParameterStartIndex != targetPath.length() - 1) {

            String queryParameterLine = targetPath.substring(queryParameterStartIndex + 1);
            String[] queryParameterElements = queryParameterLine.split(HttpSplitFormat.QUERY_PARAMETER.getValue());

            this.queryParameterInfo = createQueryParameterInfo(queryParameterElements);
            return;
        }
        this.queryParameterInfo = new HashMap<>();
    }

    private Map<String, String> createQueryParameterInfo(final String[] queryParameterElements) {
        Map<String, String> queryParameterInfo = new HashMap<>();
        for (String queryParameterElement : queryParameterElements) {
            String[] queryParameter = queryParameterElement.split(
                    HttpSplitFormat.QUERY_PARAMETER_ELEMENT.getValue());
            validateQueryParameterFormat(queryParameter);
            queryParameterInfo.put(clean(queryParameter[0]), clean(queryParameter[1]));
        }
        return queryParameterInfo;
    }

    private void validateQueryParameterFormat(final String[] queryParameter) {
        if (queryParameter == null) {
            throw new IllegalArgumentException("query parameter는 null일 수 없습니다");
        }
        if (queryParameter.length != 2) {
            throw new IllegalArgumentException("query parameter는 2개의 원소로 구성되어야 합니다");
        }
        if (queryParameter[0].isBlank()) {
            throw new IllegalArgumentException("query paramenter의 key는 빈 값일 수 없습니다");
        }
    }

    private String clean(final String target) {
        return target.trim();
    }

    public String getValue(final String target) {
        if (target == null) {
            throw new IllegalArgumentException("query parameter key는 null일 수 없습니다");
        }
        if (!queryParameterInfo.containsKey(target)) {
            throw new IllegalArgumentException("존재하지 않는 query parameter 입니다: %s".formatted(target));
        }
        return queryParameterInfo.get(target);
    }
}
