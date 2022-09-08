package nextstep.jwp.controller.resource;

import static nextstep.jwp.controller.resource.ResourceUrls.REGISTER_HTML;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class RegisterPageHandler extends ResourceHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        return generateResourceResponse(REGISTER_HTML);
    }
}
