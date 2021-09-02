package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class HelloWorldController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {

        String resource = "Hello world!";

        renderPage(request, response, resource);
    }
}
