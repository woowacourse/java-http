package org.apache.coyote.http11;

public class RequestStartLine {

    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_URL_INDEX = 1;

    private final RequestMethod requestMethod;
    private final String requestUrl;

    private RequestStartLine(String requestMethod, String requestUrl) {
        this(RequestMethod.find(requestMethod), requestUrl);
    }

    private RequestStartLine(RequestMethod requestMethod, String requestUrl) {
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
    }

    public static RequestStartLine from(String startLine) {
        final String[] startLineParts = startLine.split(" ");
        validateUrl(startLineParts);
        return new RequestStartLine(startLineParts[REQUEST_METHOD_INDEX], startLineParts[REQUEST_URL_INDEX]);
    }

    private static void validateUrl(String[] startLineParts) {
        if (startLineParts[REQUEST_URL_INDEX] == null) {
            throw new IllegalArgumentException("잘못된 형식의 요청입니다.");
        }
    }

    public boolean isGetMethod() {
        return requestMethod.equals(RequestMethod.GET);
    }

    public boolean isPostMethod() {
        return requestMethod.equals(RequestMethod.POST);
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
