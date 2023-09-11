package nextstep.jwp.controller;

import nextstep.jwp.AbstractController;
import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpStatus;

public class DefaultController extends AbstractController {
    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final var responseBody = "Hello world!";
        response.addStatus(HttpStatus.OK)
                .addContentType(ContentType.HTML)
                .addBody(responseBody);
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        throw new UnsupportedMethodException();
    }
}
