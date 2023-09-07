package nextstep.jwp.controller;

import handler.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class IndexController implements Controller {

    @Override
    public String run(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setStatusCode(HttpStatusCode.OK);
        return "/index.html";
    }
}
