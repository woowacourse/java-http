package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpHeader {

    private static final String HEADER_NAME_VALUE_SEPARATOR = ": ";

    private final Map<String, String> httpRequestHeaders = new HashMap<>();

    public HttpHeader() {
    }

    public HttpHeader(Map<String, String> httpRequestHeaders) {
        this.httpRequestHeaders.putAll(httpRequestHeaders);
    }

    public String getContentType() {
        return httpRequestHeaders.get("Content-Type");
    }

    public String getContentLength() {
        return httpRequestHeaders.get("Content-Length");
    }

    public boolean containContentLength() {
        return httpRequestHeaders.containsKey("Content-Length");
    }

    public void setHeader(String name, String value) {
        httpRequestHeaders.put(name + HEADER_NAME_VALUE_SEPARATOR, value + " ");
    }

    public String getValue() {
        List<String> headers = new LinkedList<>();
        httpRequestHeaders.forEach((key, value) -> headers.add(key + value));

        return String.join("\r\n", headers);
    }

    public String getCookie() {
        return httpRequestHeaders.get("Cookie");
    }
}
