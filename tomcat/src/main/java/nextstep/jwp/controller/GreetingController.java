package nextstep.jwp.controller;

import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import org.apache.http.HttpHeader;
import org.apache.http.HttpMime;

public class GreetingController implements Controller {

    @Override
    public void service(final Request request, final Response response) {
        response.header(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue())
                .content("Hello world!");
    }
}
