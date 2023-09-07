package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.util.PathFinder;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileController extends AbstractController {

    private static final int CONTENT_TYPE_START_INDEX = 1;

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        String requestUrl = request.getRequestUrl();
        Path path = PathFinder.findPath(requestUrl);

        String extension = requestUrl.split("\\.")[CONTENT_TYPE_START_INDEX];
        var responseBody = new String(Files.readAllBytes(path));
        ContentType contentType = ContentType.findContentType(extension);

        return new HttpResponse(HttpStatus.OK, responseBody, contentType);
    }
}
