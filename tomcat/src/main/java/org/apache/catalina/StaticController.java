package org.apache.catalina;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;

public class StaticController extends AbstractController {

    private static final String STATIC_FILE_LOCATION = "static";
    private static final String WELCOME = "Hello world!";

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        super.service(request, response);
    }

    @Override
    void doGet(Http11Request request, Http11Response response) throws Exception {
        final String path = request.getPath();
        final byte[] fileContent = readFile(path);
        final String contentType = determineContentType(path);

        response.setStaticResponse(HttpStatus.OK, fileContent, contentType);
    }

    @Override
    void doPost(Http11Request request, Http11Response response) throws Exception {
        //TODO 405 응답
    }

    private byte[] readFile(final String location) throws IOException {
        if (location.equals("/")) {
            return WELCOME.getBytes(StandardCharsets.UTF_8);
        }
        try (final InputStream fileInputStream = new FileInputStream(getClass().getClassLoader().getResource(STATIC_FILE_LOCATION + location).getPath())) {
            return fileInputStream.readAllBytes();
        } catch (final NullPointerException e) {
            throw new NoSuchFileException(location);
        }
    }

    private String determineContentType(final String path) {
        if (path.endsWith(".html")) return "text/html;charset=utf-8";
        if (path.endsWith(".css")) return "text/css;charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript;charset=utf-8";
        if (path.endsWith(".svg")) return "image/svg+xml;charset=utf-8";
        return "text/plain;charset=utf-8";
    }
}
