package nextstep.jwp.httpmessage;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> httpHeaders;

    public HttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = new HashMap<>(httpHeaders);
    }

    public String find(String key) {
        return httpHeaders.get(key);
    }

    public int size() {
        return httpHeaders.size();
    }
}
