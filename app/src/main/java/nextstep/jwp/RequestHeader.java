package nextstep.jwp;

import java.util.Map;

public class RequestHeader {
    private final String METHOD;
    private final String URI;
    private final String HTTP_VERSION;
    private final Map<String, String> HEADERS;

    public RequestHeader(String method, String uri, String httpVersion, Map<String, String> headers) {
        METHOD = method;
        URI = uri;
        HTTP_VERSION = httpVersion;
        HEADERS = headers;
    }

    public String method() {
        return METHOD;
    }

    public String uri() {
        return URI;
    }

    public String httpVersion() {
        return HTTP_VERSION;
    }

    public Map<String, String> headers() {
        return HEADERS;
    }
}
