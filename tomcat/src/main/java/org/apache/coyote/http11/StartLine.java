package org.apache.coyote.http11;

public class StartLine {

    private static final int START_LINE_LENGTH = 3;
    private static final String SPACE = " ";

    private final HttpMethod httpMethod;
    private final String uri;
    private final HttpVersion httpVersion;

    private StartLine(final HttpMethod httpMethod, final String uri, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static StartLine extractStartLine(final String startLine) {
        final String[] startLineValues = startLine.split(SPACE);

        validateStartLineCounts(startLineValues);

        final HttpMethod httpMethod = HttpMethod.parseHttpMethodFrom(startLineValues[0]);
        final String uri = startLineValues[1];
        final HttpVersion httpVersion = HttpVersion.parseHttpVersionFrom(startLineValues[2]);

        return new StartLine(httpMethod, uri, httpVersion);
    }

    private static void validateStartLineCounts(final String[] startLineValues) {
        if (startLineValues.length != START_LINE_LENGTH) {
            throw new IllegalArgumentException("StartLine의 3개의 값이 아닙니다.");
        }
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
