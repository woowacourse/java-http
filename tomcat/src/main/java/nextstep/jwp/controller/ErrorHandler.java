package nextstep.jwp.controller;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.apache.coyote.http11.request.header.ContentType.HTML;
import static org.apache.coyote.http11.request.header.HeaderKey.CONTENT_LENGTH;
import static org.apache.coyote.http11.request.header.HeaderKey.CONTENT_TYPE;
import static org.apache.coyote.http11.response.StatusLine.BAD_REQUEST;
import static org.apache.coyote.http11.response.StatusLine.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.response.StatusLine.NOT_FOUND;

public class ErrorHandler {

    private static final String BASE_PATH = "static/";
    private static final String LF = "\n";

    private ErrorHandler() {
    }

    public static void handle(final UncheckedServletException e, final HttpResponse response) throws IOException {
        handleWithStatus(response, NOT_FOUND);
    }

    public static void handle(final IllegalArgumentException e, final HttpResponse response) throws IOException {
        handleWithStatus(response, BAD_REQUEST);
    }

    public static void handle(final Exception e, final HttpResponse response) throws IOException {
        handleWithStatus(response, INTERNAL_SERVER_ERROR);
    }

    private static void handleWithStatus(final HttpResponse response, final StatusLine status) throws IOException {
        response.init();

        final String body = makeResponseBody(status.getStatusCode());
        response.setStatusLine(status);
        response.addHeader(CONTENT_TYPE, HTML.toHeaderValue());
        response.addHeader(CONTENT_LENGTH, getContentLength(body));
        response.setBody(body);
    }

    private static String makeResponseBody(final String path) throws IOException {
        final ClassLoader classLoader = ErrorHandler.class.getClassLoader();
        final String staticPath = classLoader.getResource(BASE_PATH + path + ".html").getPath();

        final List<String> fileContents = Files.readAllLines(Path.of(staticPath));
        return String.join(LF, fileContents) + LF;
    }

    private static String getContentLength(final String body) {
        return body.getBytes().length + " ";
    }
}
