package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.ResponseBuilder;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;

public class RegisterController extends AbstractController {

    private static final String PAGE_INDEX = "/index.html";
    private static final String PAGE_401 = "/401.html";
    private final UserService userService = new UserService();

    @Override
    protected HttpResponse doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String url = request.getPath() + ".html";
        final String body = readResourceBody(url);
        final ResponseHeaders responseHeaders = readResourceHeader(url, body);

        return new ResponseBuilder().status(Status.OK)
                .headers(responseHeaders)
                .body(body)
                .build();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        if (userService.register(request.getBody())) {
            return response.redirect(PAGE_INDEX);
        }
        return response.redirect(PAGE_401);
    }
}
