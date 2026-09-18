package org.apache.coyote.http;

import java.util.Optional;

public interface RequestBody {

    static RequestBody of(String contentType, String rawBody) {
        if (rawBody.isEmpty()) {
            return TextBody.EMPTY;
        }
        if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
            return FormBody.from(rawBody);
        }
        return new TextBody(rawBody);
    }

    Optional<String> get(String key);
}
