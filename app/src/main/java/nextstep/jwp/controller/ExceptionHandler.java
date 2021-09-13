package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.utils.Resources;

public class ExceptionHandler {

    public static void unauthorized(HttpResponse response) throws Exception {
        String content = FileReader.file(Resources.UNAUTHORIZED.getResource());
        addHeaders(response, content);
        response.writeStatusLine(HttpStatus.UNAUTHORIZED);
        writeHeadersAndBody(response, content);
    }

    public static void notFound(HttpResponse response) throws Exception {
        String content = FileReader.file(Resources.NOT_FOUND.getResource());
        addHeaders(response, content);
        response.writeStatusLine(HttpStatus.NOT_FOUND);
        writeHeadersAndBody(response, content);
    }

    public static void methodNotAllowed(HttpResponse response) throws Exception {
        String content = FileReader.file(Resources.METHOD_NOT_ALLOWED.getResource());
        addHeaders(response, content);
        response.writeStatusLine(HttpStatus.METHOD_NOT_ALLOWED);
        writeHeadersAndBody(response, content);
    }

    private static void writeHeadersAndBody(HttpResponse response, String content)
            throws IOException {
        response.writeHeaders();
        response.writeBody(content);
    }

    private static void addHeaders(HttpResponse response, String content) {
        response.addHeaders("Content-Type", ContentType.HTML.getContentType());
        response.addHeaders("Content-Length", String.valueOf(content.getBytes().length));
    }
}
