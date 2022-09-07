package org.apache.coyote.http11.request;

public class RequestStartLine {

    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_URL_INDEX = 1;

    private final RequestMethod method;
    private final String path;

    private RequestStartLine(String method, String path) {
        this(RequestMethod.find(method), path);
    }

    private RequestStartLine(RequestMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public static RequestStartLine from(final String startLine) {
        final String[] startLineParts = startLine.split(" ");
        validateUrl(startLineParts);
        return new RequestStartLine(startLineParts[REQUEST_METHOD_INDEX], startLineParts[REQUEST_URL_INDEX]);
    }

    private static void validateUrl(final String[] startLineParts) {
        if (startLineParts[REQUEST_URL_INDEX] == null) {
            throw new IllegalArgumentException("잘못된 형식의 요청입니다.");
        }
    }

    public boolean isGetMethod() {
        return method.equals(RequestMethod.GET);
    }

    public boolean isPostMethod() {
        return method.equals(RequestMethod.POST);
    }

    public String getPath() {
        return path;
    }
}
