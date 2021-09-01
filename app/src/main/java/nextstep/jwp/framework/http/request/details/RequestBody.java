package nextstep.jwp.framework.http.request.details;

import nextstep.jwp.framework.http.request.util.QueryParameterExtractor;

import java.util.Map;
import java.util.Objects;

public class RequestBody {

    private final Map<String, String> requestBodyMap;

    private RequestBody(final Map<String, String> requestBodyMap) {
        this.requestBodyMap = requestBodyMap;
    }

    public static RequestBody asQueryString(final String queryStringFormat) {
        if (Objects.isNull(queryStringFormat) || queryStringFormat.isEmpty()) {
            return null;
        }
        final Map<String, String> requestBody = QueryParameterExtractor.extract(queryStringFormat);
        return new RequestBody(requestBody);
    }

    public String find(final String key) {
        return requestBodyMap.get(key);
    }
}
