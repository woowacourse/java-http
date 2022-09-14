package nextstep.jwp.controller;

import org.apache.coyote.support.Request;
import org.apache.coyote.support.Response;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpMime;

public class GreetingController extends AbstractController {

    @Override
    public void doGet(final Request request, final Response response) {
        response.header(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue())
                .content("Hello world!");
    }
}
