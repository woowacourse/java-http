package nextstep.jwp.framework.request.details;

import nextstep.jwp.framework.request.util.QueryParameterExtractor;

import java.util.Map;
import java.util.Objects;

public class RequestBody {

    private final Map<String, String> requestBodyMap;

    private RequestBody(Map<String, String> requestBodyMap) {
        this.requestBodyMap = requestBodyMap;
    }

    public static RequestBody of(String queryStringFormat) {
        if (Objects.isNull(queryStringFormat) || queryStringFormat.isEmpty()) {
            return null;
        }
        final Map<String, String> requestBody = QueryParameterExtractor.extract(queryStringFormat);
        return new RequestBody(requestBody);
    }

    public String find(String key) {
        return requestBodyMap.get(key);
    }
}
