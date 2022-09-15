package nextstep.jwp.controller;

import static org.apache.coyote.http11.handler.StaticResourceHandler.readFile;

import java.io.IOException;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    public HttpResponse service(final HttpRequest request) throws IOException {
        return HttpResponse.ok(request.getHttpVersion(), request.getContentType(), readFile(request.getHttpPath()));
    }
}
