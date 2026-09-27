package org.apache.coyote.http11;

public record RequestLine(String method, String path, String protocolVersion) {

    public static RequestLine parse(final String rawRequestLine) {
        if (rawRequestLine == null) {
            return null;
        }

        final String[] parsedRequestLine = rawRequestLine.split("\\s+", 3);

        final String method = parsedRequestLine[0].trim();
        final String path = parsedRequestLine[1].trim();
        final String protocolVersion = parsedRequestLine[2].trim();

        return new RequestLine(method, path, protocolVersion.isBlank() ? "HTTP/1.1" : protocolVersion);
    }

    public boolean isGetMethod() {
        return method.equalsIgnoreCase("GET");
    }

    public boolean isPostMethod() {
        return method.equalsIgnoreCase("POST");
    }
}
