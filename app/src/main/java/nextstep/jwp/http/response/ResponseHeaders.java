package nextstep.jwp.http.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static nextstep.jwp.common.LineSeparator.NEW_LINE;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this.headers = new ConcurrentHashMap<>();
    }

    public void put(String key, String value) {
        headers.put(key, value);
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
