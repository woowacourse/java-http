package org.apache.coyote.http11.request.body;

import java.util.Objects;

public class RequestBody {

    private final String requestBody;

    private RequestBody(final String requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody from(final String requestBody) {
        return new RequestBody(requestBody);
    }

    public String getRequestBody() {
        return requestBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestBody that = (RequestBody) o;
        return Objects.equals(requestBody, that.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestBody);
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "requestBody='" + requestBody + '\'' +
                '}';
    }
}
