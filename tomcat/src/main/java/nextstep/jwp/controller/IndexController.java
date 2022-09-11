package nextstep.jwp.controller;

import static nextstep.jwp.controller.ResourceUrls.INDEX_HTML;
import static nextstep.jwp.controller.ResourceUrls.LOGIN_HTML;
import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import nextstep.jwp.application.AuthorizeService;
import nextstep.jwp.controller.exception.NotFoundControllerException;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class IndexController extends ResourceController {

    private final AuthorizeService authorizeService = AuthorizeService.getInstance();

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return doGet(httpRequest);
        }

        throw new NotFoundControllerException("해당하는 URL의 컨트롤러를 찾을 수 없습니다.");
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        if (authorizeService.isAuthorized(httpRequest)) {
            return generateResourceResponse(INDEX_HTML.getValue());
        }

        final HttpHeader location = HttpHeader.of(LOCATION.getValue(), LOGIN_HTML.getValue());
        return HttpResponse.of(HTTP11, REDIRECT, location);
    }
}
