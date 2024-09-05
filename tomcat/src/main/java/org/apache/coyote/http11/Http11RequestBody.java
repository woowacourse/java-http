package org.apache.coyote.http11;

public class Http11RequestBody {

    private final String value;

    public Http11RequestBody(String requestBody) {
        this.value = requestBody;
    }

    public String getValue() {
        return value;
    }
}
