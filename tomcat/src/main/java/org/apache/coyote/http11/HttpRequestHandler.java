package org.apache.coyote.http11;

public class HttpRequestHandler {

    private static final String MESSAGE_DELIMITER = " ";
    private static final int METHOD = 0;
    private static final int URI = 1;
    private static final int VERSION = 2;

    private HttpRequestHandler() {
    }

    public static HttpRequest newHttpRequest(final String startLine) {
        String[] split = startLine.split(MESSAGE_DELIMITER);
        return new HttpRequest(HttpMethod.valueOf(split[METHOD]), split[URI], HttpVersion.of(split[VERSION]));
    }
}
