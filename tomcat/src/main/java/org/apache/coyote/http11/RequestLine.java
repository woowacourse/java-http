package org.apache.coyote.http11;

public record RequestLine(String method, String target) {

    public static RequestLine parse(String text) {
        String[] parts = text.split(" ", 3);

        return new RequestLine(parts[0], parts[1]);
    }
}
