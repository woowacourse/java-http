package nextstep.jwp.controller;

import static nextstep.jwp.controller.ResourceUrls.INDEX_HTML;
import static nextstep.jwp.controller.ResourceUrls.LOGIN_HTML;
import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import nextstep.jwp.application.AuthorizeService;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class IndexController extends ResourceController {

    private final AuthorizeService authorizeService = AuthorizeService.getInstance();

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        if (authorizeService.isAuthorized(httpRequest)) {
            return generateResourceResponse(INDEX_HTML.getValue());
        }

        final HttpHeader location = HttpHeader.of(LOCATION.getValue(), LOGIN_HTML.getValue());
        return HttpResponse.of(HTTP11, REDIRECT, location);
    }
}
