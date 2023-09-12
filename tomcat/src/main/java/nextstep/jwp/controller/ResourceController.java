package nextstep.jwp.controller;

import java.net.URL;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.exception.PageNotFoundException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class ResourceController extends AbstractController {
    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new MethodNotAllowedException(request.getPath(), request.getMethod());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final URL resource = getClass().getClassLoader().getResource("static/" + request.getPath());
        if (resource == null) {
            throw new PageNotFoundException(request.getPath());
        }
        response.setBody(resource);
    }
}
