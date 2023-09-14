package nextstep.jwp.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootGetController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setStatusCode(StatusCode.OK)
                .setContentType(ContentType.HTML)
                .setBody("Hello world!");
    }
}
