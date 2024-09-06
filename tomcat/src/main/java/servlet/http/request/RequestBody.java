package servlet.http.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class RequestBody {

    public static final RequestBody EMPTY = new RequestBody(Collections.emptyMap());

    private final Map<String, String> body;

    public static RequestBody from(String bodies) {
        return Arrays.stream(bodies.split("&"))
                .map(body -> body.split("=", 2))
                .collect(collectingAndThen(toMap(b -> b[0], b -> b[1]), RequestBody::new));
    }

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public String getValue(String key) {
        if (!body.containsKey(key)) {
            throw new IllegalArgumentException("Request body가 존재하지 않습니다.");
        }
        return body.get(key);
    }
}
