package nextstep.jwp.controller;

import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class MainController implements Controller {

    @Override
    public HttpResponse process(HttpRequest httpRequest) {
        return HttpResponse.from(HttpStatus.OK, ContentType.HTML, "Hello world!");
    }
}
