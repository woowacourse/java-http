package nextstep.jwp.controller;

import org.apache.catalina.controller.HttpController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.*;

import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.common.MediaType.TEXT_HTML;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;

public class RootController extends HttpController {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final Set<String> requestType = Set.of("/");
        return requestType.contains(httpRequest.getTarget());
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.addHeader(CONTENT_TYPE, TEXT_HTML.stringifyWithUtf());
        httpResponse.setStatusCode(OK);
        httpResponse.setBody("Hello world!");
    }
}
