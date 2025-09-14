package org.apache.coyote.http.response;

import java.nio.charset.StandardCharsets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponseBody {

    private final byte[] data;

    public static HttpResponseBody from(final byte[] data) {
        if (data == null) {
            return HttpResponseBody.empty();
        }
        return new HttpResponseBody(data);
    }

    public static HttpResponseBody empty() {
        return new HttpResponseBody(new byte[0]);
    }

    public int getContentLength() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    @Override
    public String toString() {
        return new String(data, StandardCharsets.UTF_8);
    }
}
