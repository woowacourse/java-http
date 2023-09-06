package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.FileIOUtils;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class ResourceController extends AbstractController {

    private static final String PREFIX = "static";

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.setHttpResponseStartLine(StatusCode.OK);

        Path path = FileIOUtils.getPath(PREFIX + request.getPath());

        if (path == null || !path.toFile().isFile()) {
            response.sendRedirect("/401.html");
            return;
        }

        response.addHeader(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path) + "; charset=utf-8");
        response.setResponseBody(Files.readAllBytes(path));
    }
}
