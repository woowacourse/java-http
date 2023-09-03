package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class WebServlet extends HttpServlet {

    @Override
    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException, URISyntaxException {
        Path path = Path.of(Thread.currentThread()
                .getContextClassLoader()
                .getResource("static" + req.getPath()).toURI());

        resp.setHttpResponseStartLine(StatusCode.OK);
        resp.setBody(Files.readAllBytes(path));
        resp.addHeader("Content-Type", Files.probeContentType(path) + "; charset=utf-8");
    }

    @Override
    public void doPost(final HttpRequest req, final HttpResponse resp) {

    }
}
