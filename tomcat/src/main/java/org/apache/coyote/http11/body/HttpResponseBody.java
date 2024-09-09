package org.apache.coyote.http11.body;

public class HttpResponseBody {

    private String value;

    public HttpResponseBody() {
        this.value = "";
    }

    public CharSequence getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
