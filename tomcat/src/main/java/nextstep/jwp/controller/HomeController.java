package nextstep.jwp.controller;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class HomeController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.isGetMethod()) {
            return new HttpResponse.Builder()
                    .status(HttpStatus.OK)
                    .contentType("text/html")
                    .responseBody("Hello world!")
                    .build();
        }

        throw new UncheckedServletException("지원하지 않는 메서드입니다.");
    }
}
