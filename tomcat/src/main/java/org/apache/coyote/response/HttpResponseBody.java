package org.apache.coyote.response;

public class HttpResponseBody {

    private String value;

    public HttpResponseBody(String value) {
        this.value = value;
    }

    public boolean hasBody() {
        return this.value != null || !this.value.isEmpty();
    }

    public void update(String newValue) {
        this.value = newValue;
    }

    public String getValue() {
        return value;
    }
}
