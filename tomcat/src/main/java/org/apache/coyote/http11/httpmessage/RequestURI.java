package org.apache.coyote.http11.httpmessage;

public class RequestURI {

    private final String value;

    public static RequestURI from(String value) {
        return new RequestURI(value);
    }

    private RequestURI(String value) {
        this.value = value;
    }

    public String absolutePath() {
        if (hasQueryParameters()) {
            return value.substring(0, value.indexOf("?"));
        }
        return value;
    }

    public String[] queryParameters() {
        return value.substring(value.indexOf("?") + 1)
                .split("&");
    }

    public boolean hasQueryParameters() {
        return value.contains("?");
    }
}
