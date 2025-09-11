package org.apache.catalina.handler;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpContentType;
import org.apache.coyote.http11.HttpStatus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;

public class StaticHandler extends AbstractController {

    private static final String STATIC_FILE_LOCATION = "static";
    private static final String WELCOME = "Hello world!";

    @Override
    public void service(final Http11Request request, final Http11Response response) throws Exception {
        super.service(request, response);
    }

    @Override
    void doGet(final Http11Request request, final Http11Response response) throws Exception {
        final String path = request.getPath();
        final byte[] fileContent = readFile(path);
        final String contentType = HttpContentType.fromExtension(path).getValue();

        response.setResponse(HttpStatus.OK, fileContent, contentType);
    }

    @Override
    void doPost(final Http11Request request, final Http11Response response) throws Exception {
        response.setResponse(HttpStatus.METHOD_NOT_ALLOWED, new byte[0], "");
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
}
