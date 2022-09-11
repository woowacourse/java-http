package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class MainController implements Controller {

    @Override
    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(ContentType.HTML, "Hello world!");
    }
}
