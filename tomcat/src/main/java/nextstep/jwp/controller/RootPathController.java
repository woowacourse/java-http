package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.response.header.HttpStatusCode.OK;

import java.io.IOException;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;

public class RootPathController extends AbstractController {

    private static final String PATH = "/";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathOf(PATH) && httpRequest.hasHttpMethodOf(GET);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setHttpStatusCode(OK);
        httpResponse.setResponseBody("Hello world!");
        httpResponse.addHeader(ContentType.TEXT_PLAIN);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new ResourceNotFoundException();
    }
}
