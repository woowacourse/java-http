package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.UnsupportedMethodException;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.header.ContentType;

public class ResourceController extends AbstractController {

    private static final ResourceController INSTANCE = new ResourceController();

    public static ResourceController getINSTANCE() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws IOException, URISyntaxException {
        final String path = httpRequest.getPath();
        return HttpResponse.ofOk(ContentType.of(path), ResourceLoader.getStaticResource(path));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        throw new UnsupportedMethodException();
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isForResource();
    }

    private ResourceController() {
    }
}
