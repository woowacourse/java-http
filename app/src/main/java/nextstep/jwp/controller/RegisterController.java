package nextstep.jwp.controller;

import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;
import nextstep.jwp.HttpStatus;
import nextstep.jwp.StaticFileReader;

public class RegisterController implements Controller {

    @Override
    public HttpResponse get(HttpRequest request) {
        StaticFileReader staticFileReader = new StaticFileReader();
        String htmlOfRegister = staticFileReader.read("static/register.html");
        return new HttpResponse(HttpStatus.OK, htmlOfRegister);
    }

    @Override
    public HttpResponse post(HttpRequest request) {

        return null;
    }
}
