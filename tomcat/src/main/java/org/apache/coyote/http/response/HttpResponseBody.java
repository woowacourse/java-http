package org.apache.coyote.http.response;

import java.nio.charset.StandardCharsets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponseBody {

    private final String content;

    public static HttpResponseBody from(final String content) {
        if (content == null) {
            return HttpResponseBody.empty();
        }
        return new HttpResponseBody(content);
    }

    public static HttpResponseBody empty() {
        return new HttpResponseBody("");
    }

    public int getContentLength() {
        if (content == null) {
            return 0;
        }
        return content.getBytes(StandardCharsets.UTF_8).length;

    }

    @Override
    public String toString() {
        return content;
    }
}
