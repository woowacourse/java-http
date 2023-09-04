package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.FileIOUtils;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

//FIXME: 스프링 에러 처리는 Redirect를 사용하는 것이 아니라 RequestDispatcher를 통해 처리된다
public class ResourceController extends HttpServlet {

    private static final String PREFIX = "static";

    @Override
    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException {
        resp.setHttpResponseStartLine(StatusCode.OK);

        Path path = FileIOUtils.getPath(PREFIX + req.getPath());

        if (path == null || !path.toFile().isFile()) {
            resp.sendRedirect("/401.html");
        }

        resp.addHeader(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path) + "; charset=utf-8");
        resp.setResponseBody(Files.readAllBytes(path));
    }
}
