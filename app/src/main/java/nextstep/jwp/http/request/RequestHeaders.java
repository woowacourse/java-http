package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestHeaders {
    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders of(List<String> lines) {
        Map<String, String> headers = new HashMap<>();

        for (String line : lines) {
            String[] pair = line.split(": ");
            if (pair.length != 2) {
                break;
            }
            headers.put(pair[0], pair[1]);
        }
        return new RequestHeaders(headers);
    }

    public boolean hasContent() {
        String contentLength = headers.get("Content-Length");
        return !Objects.isNull(contentLength);
    }

    public int contentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }

    public String cookie() {
        return headers.get("Cookie");
    }
}
