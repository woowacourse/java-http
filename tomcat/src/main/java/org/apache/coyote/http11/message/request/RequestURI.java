package org.apache.coyote.http11.message.request;

public class RequestURI {

    private final String value;

    private RequestURI(String value) {
        this.value = value;
    }

    public static RequestURI from(String value) {
        return new RequestURI(value);
    }

    public String absolutePath() {
        if (hasQueryParameters()) {
            return value.substring(0, value.indexOf("?"));
        }
        return value;
    }

    public boolean hasQueryParameters() {
        return value.contains("?");
    }
}
