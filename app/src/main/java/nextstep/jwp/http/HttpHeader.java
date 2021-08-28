package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private static final String HEADER_DELIMITER = ":";

    private Map<String, String> httpHeaders;

    public HttpHeader(Map<String, String> httpHeaders) {
        this.httpHeaders = new HashMap<>(httpHeaders);
    }

    public Map<String, String> getAllHeaders() {
        return Collections.unmodifiableMap(this.httpHeaders);
    }
}
