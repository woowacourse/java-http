package org.apache.coyote.http11.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.HashMap;
import java.util.Map;

public class Http11RequestBody {

    private final String value;

    public Http11RequestBody(String requestBody) {
        this.value = requestBody;
    }

    public Map<String, String> parseBody() {
        if (!exists()) {
            throw new UncheckedServletException(new UnsupportedOperationException("Body 가 존재하지 않는 요청입니다."));
        }
        Map<String, String> param = new HashMap<>();
        for (String query : value.split("&")) {
            String key = query.split("=")[0];
            String value = query.split("=")[1];
            param.put(key, value);
        }
        return param;
    }

    private boolean exists() {
        return value.length() > 0;
    }
}
