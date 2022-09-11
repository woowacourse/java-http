package org.apache.coyote.http11.request;

public class RequestBody {

    private final String value;

    private RequestBody(String value) {
        this.value = value;
    }

    public static RequestBody from(char[] body) {
        return new RequestBody(new String(body));
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "body='" + value + '\'' +
                '}';
    }
}
