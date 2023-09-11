package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.FileIOUtils;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.response.StatusCode;

public class ResourceController extends AbstractController {

    private static final String PREFIX = "static";

    @Override
    public void doGet(final Request request, final Response response) throws IOException {
        response.setHttpResponseStartLine(StatusCode.OK);

        Path path = FileIOUtils.getPath(PREFIX + request.getPath());

        if (path == null || !path.toFile().isFile()) {
            response.sendRedirect("/404.html");
            return;
        }

        response.addHeader(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path) + "; charset=utf-8");
        response.setResponseBody(Files.readAllBytes(path));
    }
}
