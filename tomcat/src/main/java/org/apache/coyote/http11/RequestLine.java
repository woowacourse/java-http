package org.apache.coyote.http11;

public record RequestLine(
        String method,
        String target,
        String version
) {
    public static RequestLine parse(String text) {
        String[] parts = text.split(" ", 3);

        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    public String path() {
        int queryIndex = target.indexOf("?");

        if (queryIndex == -1) {
            return target;
        }

        return target.substring(0, queryIndex);
    }
}
