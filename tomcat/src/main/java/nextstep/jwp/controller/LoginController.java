package nextstep.jwp.controller;

import nextstep.jwp.handler.LoginHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.ResponseBuilder;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String url = request.getUrl() + ".html";
        final String body = readResourceBody(url);
        final ResponseHeaders responseHeaders = readResourceHeader(url, body);

        return new ResponseBuilder().status(Status.OK)
                .headers(responseHeaders)
                .body(body)
                .build();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request, final HttpResponse response) throws Exception {

        if (LoginHandler.handle(request, response)) {
            return response.redirect("/index.html");
        }

        return response.redirect("/401.html");
    }
}
