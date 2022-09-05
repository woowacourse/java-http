package nextstep.jwp.http;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestBody {

    private static final String REQUEST_BODY_DELIMITER = "&";
    private static final String REQUEST_BODY_VALUE_DELIMITER = "=";

    private static final int REQUEST_BODY_KEY_INDEX = 0;
    private static final int REQUEST_BODY_VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpRequestBody(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpRequestBody from(final String requestBody) {
        return new HttpRequestBody(convertRequestBodyMap(requestBody));
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(new ConcurrentHashMap<>());
    }

    private static Map<String, String> convertRequestBodyMap(final String requestBody) {
        Map<String, String> values = new ConcurrentHashMap<>();
        if (requestBody.isEmpty()) {
            return values;
        }
        String[] requestBodyValues = requestBody.split(REQUEST_BODY_DELIMITER);
        for (String requestBodyValue : requestBodyValues) {
            String[] value = requestBodyValue.split(REQUEST_BODY_VALUE_DELIMITER);
            values.put(value[REQUEST_BODY_KEY_INDEX], value[REQUEST_BODY_VALUE_INDEX]);
        }
        return values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpRequestBody that = (HttpRequestBody) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
