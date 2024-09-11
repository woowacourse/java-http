package org.apache.coyote.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String DATA_DELIMITER = "&";
    private static final String NAME_VALUE_DELIMITER = "=";

    private final Map<String, String> requestBody;

    public RequestBody(String rawRequestBody) {
        if (rawRequestBody == null || rawRequestBody.isEmpty()) {
            this.requestBody = Collections.emptyMap();
            return;
        }

        this.requestBody = Arrays.stream(rawRequestBody.split(DATA_DELIMITER))
                .collect(Collectors.toMap(this::parseKey, this::parseValue));
    }

    private String parseKey(String rawData) {
        validateBodyFormat(rawData);
        return rawData.substring(0, rawData.indexOf(NAME_VALUE_DELIMITER));
    }

    private String parseValue(String rawData) {
        validateBodyFormat(rawData);
        return rawData.substring(rawData.indexOf(NAME_VALUE_DELIMITER) + NAME_VALUE_DELIMITER.length());
    }

    private void validateBodyFormat(String rawData) {
        if (rawData == null || rawData.isBlank()) {
            throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
        }

        if (!rawData.contains(NAME_VALUE_DELIMITER) || rawData.startsWith(NAME_VALUE_DELIMITER)) {
            throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
        }
    }

    public boolean containsExactly(String... names) {
        Set<String> nameSet = Set.of(names);
        Set<String> keySet = requestBody.keySet();

        return nameSet.containsAll(keySet) && keySet.containsAll(nameSet);
    }

    public String get(String name) {
        return requestBody.get(name);
    }
}
