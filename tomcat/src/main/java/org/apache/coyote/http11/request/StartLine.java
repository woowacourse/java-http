package org.apache.coyote.http11.request;

public class StartLine {

    private static final String LINE_REGEX = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URL_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int START_LINE_SIZE = 3;

    private final String httpMethod;
    private final RequestURL requestURL;
    private final String protocol;

    private StartLine(final String httpMethod, final String requestURL, final String protocol) {
        this.httpMethod = httpMethod;
        this.requestURL = RequestURL.from(requestURL);
        this.protocol = protocol;
    }

    public static StartLine from(final String line) {
        final String[] values = line.split(LINE_REGEX);
        validateStartLine(values);
        return new StartLine(
                values[HTTP_METHOD_INDEX],
                values[REQUEST_URL_INDEX],
                values[HTTP_VERSION_INDEX]
        );
    }

    private static void validateStartLine(final String[] values) {
        if (values.length < START_LINE_SIZE) {
            throw new IllegalArgumentException("비어있는 값이 있습니다.");
        }
    }

    public boolean isGet() {
        return "GET".equals(httpMethod);
    }

    public boolean isPost() {
        return "POST".equals(httpMethod);
    }

    public RequestURL getRequestURL() {
        return requestURL;
    }
}
