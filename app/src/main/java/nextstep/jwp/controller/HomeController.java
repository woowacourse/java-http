package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestURI;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        RequestURI requestURI = request.getRequestURI();
        response.forward(requestURI.getRequestURI());
    }
}
