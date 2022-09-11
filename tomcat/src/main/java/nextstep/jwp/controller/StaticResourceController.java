package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Location;

public class StaticResourceController extends AbstractController {

    @Override
    public HttpResponse service(final HttpRequest request) throws IOException {
        return new HttpResponse(request.getHttpVersion(), HttpStatus.OK, Location.empty(), request.getContentType(),
                readFile(request.getHttpPath()));
    }
}
