package servlet.http.request;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class RequestBody {

    private static final RequestBody EMPTY = new RequestBody(Collections.emptyMap());
    private static final String BODY_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int UNLIMITED_SPLIT = -1;
    private static final int VALID_KEY_VALUE_LENGTH = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(String bodies) {
        if (isEmptyBody(bodies)) {
            return EMPTY;
        }
        return Arrays.stream(bodies.split(BODY_DELIMITER))
                .map(RequestBody::splitKeyValue)
                .collect(collectingAndThen(
                        toMap(b -> b[KEY_INDEX].strip(), b -> b[VALUE_INDEX].strip()), RequestBody::new)
                );
    }

    private static boolean isEmptyBody(String bodies) {
        return bodies == null || bodies.isBlank();
    }

    private static String[] splitKeyValue(String body) {
        String[] keyValue = body.split(KEY_VALUE_DELIMITER, UNLIMITED_SPLIT);
        if (keyValue.length != VALID_KEY_VALUE_LENGTH) {
            throw new IllegalArgumentException("잘못된 Body입니다. body: '%s'".formatted(body));
        }
        validateNotBlankKeyValue(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        return keyValue;
    }

    private static void validateNotBlankKeyValue(String key, String value) {
        if (key.isBlank() || value.isBlank()) {
            throw new IllegalArgumentException("key 또는 value가 비어있습니다. key: '%s', value: '%s'".formatted(key, value));
        }
    }

    protected String getValue(String key) {
        validateNotEmptyBody();
        if (!body.containsKey(key)) {
            throw new IllegalArgumentException("Request body에 해당 key가 존재하지 않습니다. key: %s".formatted(key));
        }
        return body.get(key);
    }

    private void validateNotEmptyBody() {
        if (body.isEmpty()) {
            throw new IllegalArgumentException("Request body가 비어있습니다.");
        }
    }
}
