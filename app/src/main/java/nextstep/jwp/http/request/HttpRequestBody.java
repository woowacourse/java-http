package nextstep.jwp.http.request;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

public class HttpRequestBody {
    private final String value;

    public HttpRequestBody(String value) {
        this.value = value;
    }

    public Map<String, String> parsedBody() {
        return Arrays.stream(value.split("&"))
            .map(singleBody -> singleBody.split("="))
            .collect(toMap(unitBody -> unitBody[0], unitBody -> unitBody[1]));
    }
}
