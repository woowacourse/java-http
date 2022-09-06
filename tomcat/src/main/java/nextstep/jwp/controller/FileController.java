package nextstep.jwp.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.PathFinder;

public class FileController extends AbstractController {

    private static final int CONTENT_TYPE_START_INDEX = 1;

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final String requestUrl = request.getRequestUrl();
        final Path path = PathFinder.findPath(requestUrl);

        final String extension = requestUrl.split("\\.")[CONTENT_TYPE_START_INDEX];
        final var responseBody = new String(Files.readAllBytes(path));
        final ContentType contentType = ContentType.findContentType(extension);

        return new HttpResponse(HttpStatus.OK, responseBody, contentType);
    }
}
