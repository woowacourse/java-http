package org.apache.coyote.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String DATA_DELIMITER = "&";
    private static final String NAME_VALUE_DELIMITER = "=";

    private final Map<String, String> requestBody;

    public RequestBody(String rawRequestBody) {
        this.requestBody = Arrays.stream(rawRequestBody.split(DATA_DELIMITER))
                .peek(rawData -> {
                    if (!rawData.contains(NAME_VALUE_DELIMITER)) {
                        throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
                    }
                })
                .collect(Collectors.toMap(
                        data -> data.substring(0, data.indexOf(NAME_VALUE_DELIMITER)),
                        data -> data.substring(data.indexOf(NAME_VALUE_DELIMITER) + 1)
                ));
    }

    public boolean containsAll(String... names) {
        return Arrays.stream(names)
                .allMatch(requestBody::containsKey);
    }

    public String get(String name) {
        return requestBody.get(name);
    }
}
