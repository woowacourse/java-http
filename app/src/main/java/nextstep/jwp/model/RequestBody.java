package nextstep.jwp.model;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestBody {

    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public Map<String, String> queries() {
        return Stream.of(body.split("&"))
                .map(x -> x.split("="))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));
    }
}
