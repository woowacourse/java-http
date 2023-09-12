package nextstep.jwp.controller;

import static nextstep.servlet.StaticResourceResolver.HOME_PAGE;

import org.apache.coyote.http11.message.HttpStatusCode;
import org.apache.coyote.http11.message.request.HttpRequest;

public class HomeController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.isPathMatch("/") || request.isPathMatch("/index.html");
    }

    @Override
    ResponseEntity doGet() {
        return ResponseEntity.forward(HttpStatusCode.OK, HOME_PAGE);
    }

    @Override
    ResponseEntity doPost(HttpRequest request) {
        throw new IllegalArgumentException("지원하지 않는 HTTP Method 입니다.");
    }
}
