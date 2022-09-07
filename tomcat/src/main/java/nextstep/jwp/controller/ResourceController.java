package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws IOException, URISyntaxException {
        return HttpResponse.ofResource(httpRequest.getPath());
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        throw new UnsupportedMethodException();
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isForResource();
    }
}
