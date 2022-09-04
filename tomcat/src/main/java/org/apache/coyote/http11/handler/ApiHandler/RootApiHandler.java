package org.apache.coyote.http11.handler.ApiHandler;

import java.util.regex.Pattern;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class RootApiHandler implements Handler {

    private static final Pattern ROOT_URI_PATTERN = Pattern.compile("/");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchUri(ROOT_URI_PATTERN);
    }

    @Override
    public ApiHandlerResponse getResponse(HttpRequest httpRequest) {
        return new ApiHandlerResponse(HttpStatus.OK, "Hello world!");
    }
}
