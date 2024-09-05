package org.apache.coyote.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private final Map<String, String> requestBody;

    public RequestBody(String rawRequestBody) {
        this.requestBody = Arrays.stream(rawRequestBody.split("&"))
                .peek(rawData -> {
                    if (!rawData.contains("=")) {
                        throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
                    }
                })
                .collect(Collectors.toMap(
                        data -> data.substring(0, data.indexOf("=")),
                        data -> data.substring(data.indexOf("=") + 1)
                ));
    }

    public boolean containsAll(String... names) {
        for (String name : names) {
            if (!requestBody.containsKey(name)) {
                return false;
            }
        }
        return true;
    }

    public String get(String name) {
        return requestBody.get(name);
    }
}
