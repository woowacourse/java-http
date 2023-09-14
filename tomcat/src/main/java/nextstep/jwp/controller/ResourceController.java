package nextstep.jwp.controller;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.apache.coyote.http11.request.header.HeaderKey.CONTENT_LENGTH;
import static org.apache.coyote.http11.request.header.HeaderKey.CONTENT_TYPE;

public class ResourceController extends AbstractController {

    private static final String HELLO_WORLD = "Hello world!";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        try {
            final String body = makeResponseBody(request);

            response.setStatusLine(StatusLine.OK);
            response.addHeader(CONTENT_TYPE, request.getContentType());
            response.addHeader(CONTENT_LENGTH, getContentLength(body));
            response.setBody(body);
        } catch (final IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String getContentLength(final String body) {
        return body.getBytes().length + " ";
    }

    private String makeResponseBody(final HttpRequest request) throws IOException {
        if (request.getPath().toString().equals("static/.html")) {
            return HELLO_WORLD;
        }

        final List<String> fileContents = Files.readAllLines(request.getPath());
        return String.join("\n", fileContents) + "\n";
    }
}
