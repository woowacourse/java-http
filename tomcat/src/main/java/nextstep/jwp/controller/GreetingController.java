package nextstep.jwp.controller;

import org.apache.http.*;

public class GreetingController implements Controller {

    @Override
    public ResponseEntity execute(final RequestEntity request) {
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());
        return new ResponseEntity(headers)
                .content("Hello world!");
    }
}
