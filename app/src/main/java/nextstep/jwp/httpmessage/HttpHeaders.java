package nextstep.jwp.httpmessage;

import java.util.LinkedHashMap;
import java.util.Map;

import static nextstep.jwp.httpmessage.httprequest.HttpMessageReader.CRLF;
import static nextstep.jwp.httpmessage.httprequest.HttpMessageReader.SP;

public class HttpHeaders {

    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers;

    public HttpHeaders() {
        this(new LinkedHashMap<>());
    }

    public HttpHeaders(Map<String, String> httpHeaders) {
        this.headers = new LinkedHashMap<>(httpHeaders);
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
            temp.append(header.getKey()).append(HEADER_DELIMITER).append(header.getValue()).append(SP).append(CRLF);
        }
        return temp.toString();
    }
}
