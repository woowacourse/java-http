package nextstep.jwp.controller;

import java.io.File;
import java.net.URL;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        if (hasStaticResourceFile(request.getUriPath())) {
            return HttpResponse.ok().fileBody(request.getUriPath()).build();
        }
        return HttpResponse.notFound().build();
    }

    private boolean hasStaticResourceFile(final String uri) {
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return resource != null && new File(resource.getFile()).isFile();
    }
}
