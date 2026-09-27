package org.apache.coyote.http11;

public record RequestLine(String method, String path, String query, String version) {

    public static RequestLine parse(String raw) {
        String[] parts = raw.split(" ", 3);
        String uri = parts[1];
        String path = uri;
        String query = "";

        int questionMark = uri.indexOf('?');
        if (questionMark >= 0) {
            path = uri.substring(0, questionMark);
            query = uri.substring(questionMark + 1);
        }

        return new RequestLine(parts[0], path, query, parts[2]);
    }
}
