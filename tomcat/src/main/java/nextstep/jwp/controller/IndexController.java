package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.ResponseBuilder;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;

public class IndexController extends AbstractController {

    private static final String INDEX_HTML = "/index.html";

    @Override
    protected HttpResponse doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String body = readResourceBody(INDEX_HTML);
        final ResponseHeaders responseHeaders = readResourceHeader(INDEX_HTML, body);

        return new ResponseBuilder().status(Status.OK)
                .headers(responseHeaders)
                .body(body)
                .build();
    }
}
