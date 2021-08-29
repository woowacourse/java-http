package nextstep.jwp.request;

import java.util.Map;

public class RequestHttpHeader {
    private Map<String, String> requestHttpHeader;

    private RequestHttpHeader(Map<String, String> requestHttpHeader) {
        this.requestHttpHeader = requestHttpHeader;
    }

    public static RequestHttpHeader of(Map<String, String> requestHttpHeader) {
        return new RequestHttpHeader(requestHttpHeader);
    }
}
