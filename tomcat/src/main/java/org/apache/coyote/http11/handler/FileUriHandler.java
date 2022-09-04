package org.apache.coyote.http11.handler;

import java.util.regex.Pattern;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class FileUriHandler implements UriHandler {

    private static final Pattern FILE_URI_PATTERN = Pattern.compile("/.+\\.(html|css|js|ico)");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchUri(FILE_URI_PATTERN);
    }

    @Override
    public HandlerResponse getResponse(HttpRequest httpRequest) {
        return new HandlerResponse(HttpStatus.OK, httpRequest.getPath());
    }
}
