package org.apache.coyote.http11;

import org.apache.coyote.exception.UserNotFoundException;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpStatus;
import org.apache.util.ResourceUtil;

public class ErrorHandler {

    private static final String TEXT_HTML = "text/html";
    private static final String UNAUTHORIZED_URL = "401.html";
    private static final String INTERNAL_SERVER_URL = "500.html";

    public static HttpResponse handle(final Exception e) {
        if (e instanceof UserNotFoundException) {
            return new HttpResponse(HttpStatus.UNAUTHORIZED, TEXT_HTML, ResourceUtil.getResource(UNAUTHORIZED_URL));
        }
        return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, TEXT_HTML, ResourceUtil.getResource(
                INTERNAL_SERVER_URL));
    }
}
