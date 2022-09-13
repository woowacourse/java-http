package nextstep.jwp.controller;

import java.util.List;
import org.apache.coyote.http11.MediaType;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class HomeController extends AbstractController {

    private static final List<String> PATHS = List.of("/");
    private static final String DEFAULT_REQUEST_BODY = "Hello world!";

    @Override
    public boolean containsPath(final String path) {
        return PATHS.contains(path);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.httpStatus(HttpStatus.OK)
                .body(DEFAULT_REQUEST_BODY, MediaType.HTML);
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.redirect("/404.html");
    }
}
