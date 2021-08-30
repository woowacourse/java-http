package nextstep.jwp.domain.request;

import java.util.Map;

public class RequestBody {

    private Map<String, String> requestBodyMap;

    private RequestBody(Map<String, String> requestBodyMap) {
        this.requestBodyMap = requestBodyMap;
    }

    public static RequestBody of(Map<String, String> requestBodyMap) {
        return new RequestBody(requestBodyMap);
    }

    public String getParam(String key) {
        return requestBodyMap.get(key);
    }
}
