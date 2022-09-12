package nextstep.jwp.controller;

import org.apache.constant.MediaType;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpMessage;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Override
    public String uri() {
        return "/";
    }

    public boolean support(final String uri, final String httpMethods) {
        return super.supportInternal(uri, httpMethods, this);
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        log.info("Home Request!");
        doGet(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String welcomeMessage = "Hello world!";
        response.addStatus(HttpStatus.OK)
                .add(HttpMessage.CONTENT_TYPE, MediaType.PLAIN.value())
                .body(welcomeMessage);
    }
}
