package nextstep.jwp.http.request;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestHeaders {
    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders of(List<String> lines) {
        Map<String, String> headers = lines.stream()
                .map(line -> line.split(": "))
                .filter(pair -> pair.length == 2)
                .collect(toMap(pair -> pair[0], pair -> pair[1]));
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
