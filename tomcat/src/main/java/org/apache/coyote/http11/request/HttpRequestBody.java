package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import org.apache.util.QueryStringParser;

public class HttpRequestBody {

    private final String value;
    private final Map<String, List<String>> queryStrings;

    private HttpRequestBody(String value, Map<String, List<String>> queryStrings) {
        this.value = value;
        this.queryStrings = queryStrings;
    }

    public static HttpRequestBody create(String value) {
        if (QueryStringParser.isQueryString(value)) {
            return new HttpRequestBody(value, QueryStringParser.parseQueryString(value));
        }
        return new HttpRequestBody(value, null);
    }

    public String getValue() {
        return value;
    }

    public String getQueryParam(String param) {
        if (!queryStrings.containsKey(param)) {
            throw new IllegalArgumentException("존재하지 않는 파라미터입니다.");
        }
        List<String> values = queryStrings.get(param);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 파라미터입니다.");
        }
        return values.getFirst();
    }
}
