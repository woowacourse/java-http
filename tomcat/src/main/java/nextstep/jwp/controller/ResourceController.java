package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.FileIOUtils;
import org.apache.coyote.http11.HttpServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class ResourceController extends HttpServlet {

    private static final String PREFIX = "static";

    @Override
    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException, URISyntaxException {
        resp.setHttpResponseStartLine(StatusCode.OK);

        Path path = FileIOUtils.getPath(PREFIX + req.getPath());
        resp.setResponseBody(Files.readAllBytes(path));
        resp.addHeader("Content-Type", Files.probeContentType(path) + "; charset=utf-8");
    }
}
