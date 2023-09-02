package org.apache.coyote.http11.request;

public class StartLine {

    private static final StartLine EMPTY = new StartLine(null, null, null);

    private final String method;
    private final URI uri;
    private final String httpVersion;

    private StartLine(String method, URI uri, String httpVersion) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static StartLine from(String startLine) {
        if (startLine == null) {
            return EMPTY;
        }
        String[] elements = startLine.split(" ");
        validateStartLine(elements);
        return new StartLine(
                elements[0],
                URI.from(elements[1]),
                elements[2]
        );
    }

    private static void validateStartLine(String[] elements) {
        if (elements.length != 3) {
            throw new InvalidStartLineException();
        }
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    public String method() {
        return method;
    }

    public URI uri() {
        return uri;
    }

    public String httpVersion() {
        return httpVersion;
    }
}
