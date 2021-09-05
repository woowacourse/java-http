package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

public class ExceptionHandler {

    public static void unauthorized(HttpResponse response) throws Exception {
        String content = FileReader.file("/401.html");

        response.writeStatusLine(HttpStatus.UNAUTHORIZED);
        writeHeadersAndBody(response, content);
    }

    public static void notFound(HttpResponse response) throws Exception {
        String content = FileReader.file("/404.html");

        response.writeStatusLine(HttpStatus.NOT_FOUND);
        writeHeadersAndBody(response, content);
    }

    public static void methodNotAllowed(HttpResponse response) throws Exception {
        String content = FileReader.file("/405.html");

        response.writeStatusLine(HttpStatus.METHOD_NOT_ALLOWED);
        writeHeadersAndBody(response, content);
    }

    private static void writeHeadersAndBody(HttpResponse response, String content)
            throws IOException {
        response.writeHeaders();
        response.writeBody(content);
    }
}
