package org.apache.coyote.http11;

public record RequestLine(String method, String target, String version) {

    public static RequestLine parse(final String line) {
        final String[] parts = line.trim().split(" +", 3);
        if (parts.length != 3 || !parts[2].equals("HTTP/1.1")) {
            throw new IllegalArgumentException("Invalid HTTP/1.1 request line");
        }
        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    public String path() {
        final int separator = target.indexOf('?');
        return separator < 0 ? target : target.substring(0, separator);
    }

    public String query() {
        final int separator = target.indexOf('?');
        return separator < 0 ? "" : target.substring(separator + 1);
    }
}
