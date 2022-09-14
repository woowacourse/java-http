package nextstep.jwp.controller;

import static nextstep.jwp.controller.ResourceUrls.INDEX_HTML;
import static nextstep.jwp.controller.ResourceUrls.LOGIN_HTML;

import nextstep.jwp.application.AuthorizeService;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class IndexController extends ResourceController {

    private final AuthorizeService authorizeService = AuthorizeService.getInstance();

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (authorizeService.isAuthorized(httpRequest)) {
            setResource(INDEX_HTML.getValue(),httpResponse);
            return;
        }
        setRedirectHeader(httpResponse, LOGIN_HTML);
    }
}
