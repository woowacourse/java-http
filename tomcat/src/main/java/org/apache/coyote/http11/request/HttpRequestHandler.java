package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.model.HttpRequest;

public class HttpRequestHandler {

    private static final String MESSAGE_DELIMITER = " ";
    private static final int METHOD = 0;
    private static final int URI = 1;
    private static final int VERSION = 2;

    private HttpRequestHandler() {
    }

    public static HttpRequest newHttpRequest(final String startLine) {
        String[] split = startLine.split(MESSAGE_DELIMITER);
        return new HttpRequest(split[METHOD], split[URI], split[VERSION]);
    }
}
