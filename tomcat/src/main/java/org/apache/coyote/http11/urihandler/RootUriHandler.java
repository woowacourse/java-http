package org.apache.coyote.http11.urihandler;

import java.util.regex.Pattern;
import org.apache.coyote.http11.HandlerResponse;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class RootUriHandler implements UriHandler {

    private static final Pattern ROOT_URI_PATTERN = Pattern.compile("/");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchUri(ROOT_URI_PATTERN);
    }

    @Override
    public HandlerResponse getResponse(HttpRequest httpRequest) {
        return new HandlerResponse(HttpStatus.OK, "Hello world!");
    }
}
