package nextstep.jwp.controller;

import org.apache.http.Request;
import org.apache.http.Response;
import org.apache.http.HttpHeader;
import org.apache.http.HttpMime;

public class GreetingController extends AbstractController {

    @Override
    public void doGet(final Request request, final Response response) {
        response.header(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue())
                .content("Hello world!");
    }
}
