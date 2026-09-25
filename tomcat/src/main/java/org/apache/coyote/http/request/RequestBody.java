package org.apache.coyote.http.request;

import java.util.Optional;

public interface RequestBody {

    static RequestBody of(ContentType contentType, String rawBody) {
        if (rawBody.isEmpty()) {
            return TextBody.EMPTY;
        }
        if (contentType == ContentType.FORM_URLENCODED) {
            return FormBody.from(rawBody);
        }
        return new TextBody(rawBody);
    }

    Optional<String> get(String key);
}
