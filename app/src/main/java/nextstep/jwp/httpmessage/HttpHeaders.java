package nextstep.jwp.httpmessage;

import java.util.HashMap;
import java.util.Map;

import static nextstep.jwp.httpmessage.HttpMessageReader.CRLF;
import static nextstep.jwp.httpmessage.HttpMessageReader.SP;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders() {
        this(new HashMap<>());
    }

    public HttpHeaders(Map<String, String> httpHeaders) {
        this.headers = new HashMap<>(httpHeaders);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public int size() {
        return headers.size();
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeadersAsString() {
        StringBuilder temp = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            temp.append(header.getKey()).append(": ").append(header.getValue()).append(SP).append(CRLF);
        }
        return temp.toString();
    }
}
