package nextstep.jwp.controller;

import nextstep.jwp.http.Headers;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import org.apache.http.*;

public class GreetingController implements Controller {

    @Override
    public Response execute(final Request request) {
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());
        return new Response(headers)
                .content("Hello world!");
    }
}
