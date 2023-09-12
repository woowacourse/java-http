package nextstep.jwp.controller;

import static nextstep.servlet.StaticResourceResolver.HOME_PAGE;

import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpStatusCode;
import org.apache.coyote.http11.message.request.HttpRequest;

public class HomeController implements Controller {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getPath().equals("/") || request.getPath().equals("/index.html");
    }

    @Override
    public ResponseEntity handle(HttpRequest request) {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet();
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP Method 입니다.");
    }

    private ResponseEntity doGet() {
        return ResponseEntity.forward(HttpStatusCode.OK, HOME_PAGE);
    }
}
