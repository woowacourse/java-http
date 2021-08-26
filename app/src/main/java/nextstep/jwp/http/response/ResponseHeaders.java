package nextstep.jwp.http.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseHeaders {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this.headers = new ConcurrentHashMap<>();
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            String value = headers.get("Content-Length");
            value = value.trim();
            return Integer.parseInt(value);
        }
        return 0;
    }

    @Override
    public String toString() {
        final List<String> parsedHeaders = getParsedHeaders();
        return String.join(NEW_LINE, parsedHeaders);
    }

    private List<String> getParsedHeaders() {
        List<String> parsedHeaders = new ArrayList<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            parsedHeaders.add(entry.getKey() + ": " + entry.getValue() + " ");

        }
        return parsedHeaders;
    }
}
