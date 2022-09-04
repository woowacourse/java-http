package org.apache.coyote.http11.urihandler;

import java.util.regex.Pattern;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UriResponse;
import org.apache.coyote.http11.request.HttpRequest;

public class RootUriHandler implements UriHandler {

    private static final Pattern ROOT_URI_PATTERN = Pattern.compile("/");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchUri(ROOT_URI_PATTERN);
    }

    @Override
    public UriResponse getResponse(HttpRequest httpRequest) {
        return new UriResponse("Hello world!", ContentType.HTML.getValue());
    }
}
