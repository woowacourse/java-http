package nextstep.jwp.controller;

import nextstep.jwp.model.HttpRequest;
import nextstep.jwp.model.HttpResponse;
import nextstep.jwp.model.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        String responseBody = "Hello world!";
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", "12");
        return new HttpResponse(HttpStatus.OK, headers, responseBody);
    }
}
