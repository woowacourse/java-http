package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

import java.io.IOException;

public class NotFoundController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpResponse response = new HttpResponse.Builder()
                .outputStream(httpResponse.getOutputStream())
                .status(HttpStatus.NOT_FOUND_404)
                .resource(httpRequest.getResource())
                .body("/404.html")
                .build();
        response.forward();
    }
}
