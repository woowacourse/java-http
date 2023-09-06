package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.util.PathFinder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        if (request.getHttpMethod() == HttpMethod.GET) {
            return doGet(request);
        }
        return doPost(request);
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return defaultInternalServerErrorPage();
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return defaultInternalServerErrorPage();
    }

    private HttpResponse defaultInternalServerErrorPage() throws URISyntaxException, IOException {
        final Path path = PathFinder.findPath("/500.html");
        final String responseBody = new String(Files.readAllBytes(path));
        return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, responseBody, ContentType.HTML);
    }
}
