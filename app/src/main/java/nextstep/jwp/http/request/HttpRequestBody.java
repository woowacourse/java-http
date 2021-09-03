package nextstep.jwp.http.request;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

public class HttpRequestBody {

    private static final String BODY_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_ON_KEY_VALUE_FORMAT = 0;
    private static final int VALUE_ON_KEY_VALUE_FORMAT = 1;
    private final String value;

    public HttpRequestBody(String value) {
        this.value = value;
    }

    public Map<String, String> parsedBody() {
        return Arrays.stream(value.split(BODY_SEPARATOR))
            .map(singleBody -> singleBody.split(KEY_VALUE_SEPARATOR))
            .collect(toMap(unitBody -> unitBody[KEY_ON_KEY_VALUE_FORMAT],
                unitBody -> unitBody[VALUE_ON_KEY_VALUE_FORMAT]));
    }
}
