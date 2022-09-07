package nextstep.jwp.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.controller.Controller;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import nextstep.jwp.util.PathUtils;

public class AbstractController implements Controller {

    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        if (request.isGet()) {
            return doGet(request);
        }
        return doPost(request);
    }

    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        return notFound();
    }

    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        return notFound();
    }

    private HttpResponse notFound() throws Exception {
        final Path path = PathUtils.load(NOT_FOUND_PAGE);
        final String responseBody = new String(Files.readAllBytes(path));
        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.HTML, responseBody);
    }
}
