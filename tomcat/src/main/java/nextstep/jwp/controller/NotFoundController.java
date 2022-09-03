package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import nextstep.jwp.util.FileReader;

public class NotFoundController implements Controller {

    @Override
    public HttpResponse doService(HttpRequest request) {
        return HttpResponse.withoutLocation(
            request.getVersion(),
            "404 Not Found",
            request.getUri(),
            FileReader.read("/404.html")
        );
    }
}
