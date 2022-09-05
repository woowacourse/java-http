package org.apache.coyote.http11;

public class StartLine {

    private static final String LINE_REGEX = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URL_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int START_LINE_SIZE = 3;

    private final String httpMethod;
    private final RequestURL requestURL;
    private final String httpVersion;

    private StartLine(String httpMethod, String requestURL, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURL = RequestURL.from(requestURL);
        this.httpVersion = httpVersion;
    }

    public static StartLine from(String line) {
        String[] values = line.split(LINE_REGEX);
        validateStartLine(values);
        return new StartLine(
                values[HTTP_METHOD_INDEX],
                values[REQUEST_URL_INDEX],
                values[HTTP_VERSION_INDEX]
        );
    }

    private static void validateStartLine(String[] values) {
        if (values.length < START_LINE_SIZE) {
            throw new IllegalArgumentException("비어있는 값이 있습니다.");
        }
    }

    public boolean isMainRequest() {
        return requestURL.isMainRequest();
    }

    public boolean isLoginRequest() {
        return requestURL.isLoginRequest();
    }

    public void changeRequestURL() {
        requestURL.changeRequestURL();
    }

    public RequestURL getRequestURL() {
        return requestURL;
    }
}
