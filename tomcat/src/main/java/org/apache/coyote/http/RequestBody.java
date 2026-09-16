package org.apache.coyote.http;

public interface RequestBody {

    static RequestBody of(ContentType contentType, String rawBody) {
        if (rawBody.isEmpty()) {
            return TextBody.EMPTY;
        }
        return contentType.parse(rawBody);
    }
}
