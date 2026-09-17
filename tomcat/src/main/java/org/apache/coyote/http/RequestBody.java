package org.apache.coyote.http;

public interface RequestBody {

    static RequestBody of(String rawBody) {
        if (rawBody.isEmpty()) {
            return TextBody.EMPTY;
        }
        return new TextBody(rawBody);
    }
}
