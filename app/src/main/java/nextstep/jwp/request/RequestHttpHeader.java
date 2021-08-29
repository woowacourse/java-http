package nextstep.jwp.request;

import java.util.Map;

public class RequestHttpHeader {
    private Map<String, String> requestHttpHeaderMap;

    private RequestHttpHeader(Map<String, String> requestHttpHeaderMap) {
        this.requestHttpHeaderMap = requestHttpHeaderMap;
    }

    public static RequestHttpHeader of(Map<String, String> requestHttpHeader) {
        return new RequestHttpHeader(requestHttpHeader);
    }

    public Map<String, String> getRequestHttpHeaderMap() {
        return requestHttpHeaderMap;
    }
}
