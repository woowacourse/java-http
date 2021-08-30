package nextstep.jwp.httpmessage;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> httpHeaders) {
        this.headers = new HashMap<>(httpHeaders);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public int size() {
        return headers.size();
    }
}
