package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.StatusCode;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class HelloController extends AbstractController{

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {

        response.message("Hello world!");
    }
}
