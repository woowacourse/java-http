package nextstep.jwp.http.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    private static final String CONTENT_LENTH = "Content-Length";

    private final Map<String, String> headers;
    private final String value;

    public HttpHeaders() {
        headers = new HashMap<>();
        value = "";
    }

    public HttpHeaders(Map<String, String> headers, String value) {
        this.headers = headers;
        this.value = value;
    }

    public static HttpHeaders createByString(String value) {
        return new HttpHeaders(parseHeaders(value), value);
    }

    private static HashMap<String, String> parseHeaders(String value) {
        HashMap<String, String> headers = new HashMap<>();
        String[] lines = value.split(System.getProperty("line.separator"));
        for (String line : lines) {
            if ("".equals(line)) {
                continue;
            }
            String[] keyValue = line.split(":", 2);
            headers.putIfAbsent(keyValue[0], keyValue[1].trim());
        }
        return headers;
    }

    public Optional<String> getHeaderByName(String name) {
        String header = headers.getOrDefault(name, null);
        return Optional.ofNullable(header);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public boolean isExists(String name) {
        return headers.containsKey(name);
    }

    public boolean isExistsContentLength() {
        return isExists(CONTENT_LENTH);
    }

    public int contentLength() {
        String contentLength = getHeaderByName(CONTENT_LENTH)
            .orElseThrow();
        return Integer.parseInt(contentLength);
    }

    public String asString() {
        if ("".equals(value)) {
            return getHeadersAsString();
        }
        return value;
    }

    private String getHeadersAsString() {
        StringBuilder headersAsString = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String header = entry.getKey() + ": " + entry.getValue();
            headersAsString
                .append(header)
                .append("\r\n");
        }
        return headersAsString.toString();
    }
}
