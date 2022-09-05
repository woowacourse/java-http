package org.apache.coyote.http11.httpmessage.request;

import java.util.List;
import java.util.Objects;

public class RequestBody {

    private final String body;

    private RequestBody(String body) {
        this.body = body;
    }

    public static RequestBody of(List<String> body) {
        return new RequestBody(String.join("\r\n", body));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestBody that = (RequestBody) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
