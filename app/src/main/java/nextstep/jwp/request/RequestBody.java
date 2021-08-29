package nextstep.jwp.request;

import java.util.Map;

public class RequestBody {
    private final Map<String, String> requestBody;

    private RequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody of(String queryStringFormat) {
        final Map<String, String> requestBody = QueryParameterExtractor.extract(queryStringFormat);
        return new RequestBody(requestBody);
    }


    public String find(String key) {
        return requestBody.get(key);
    }
}
