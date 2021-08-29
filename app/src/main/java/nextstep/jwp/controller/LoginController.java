package nextstep.jwp.controller;


import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
    }
}
