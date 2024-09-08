package org.apache.coyote.request;

public record HttpRequestBody(String value) {

    public boolean hasBody() {
        return value != null || !value.isEmpty();
    }
}
