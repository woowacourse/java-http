package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapping {
    public static void mapping(HttpRequest request, HttpResponse response) throws Exception {
        String url = request.getUrl();

        if (request.isResource()) {
            new ResourceController().service(request, response);
        }
        if (url.equals("/login")) {
            new LoginController().service(request, response);
        }
        if (url.equals("/register")) {
            new RegisterController().service(request, response);
        }
        if (url.equals("/")) {
            new BaseController().service(request, response);
        }
    }
}
