package org.apache.coyote.http11.handler.resource;

import static org.apache.coyote.http11.handler.resource.ResourceUrls.INDEX_HTML;
import static org.apache.coyote.http11.handler.resource.ResourceUrls.LOGIN_HTML;
import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import nextstep.jwp.application.AuthorizeService;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class IndexHandler extends ResourceHandler {

    private final AuthorizeService authorizeService = AuthorizeService.getInstance();

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        if (authorizeService.isAuthorized(httpRequest)) {
            return generateResourceResponse(INDEX_HTML);
        }
        final HttpHeader location = HttpHeader.of(LOCATION.getValue(), LOGIN_HTML);

        return HttpResponse.of(HTTP11, REDIRECT, location);
    }
}
