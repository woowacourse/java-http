package org.apache.catalina.handler;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

public abstract class AbstractController implements Controller {

    private static final String STATIC_FILE_LOCATION = "static";

    @Override
    public void service(final Http11Request request, final Http11Response response) throws Exception {
        if (request.getMethod().equals("GET")) {
            doGet(request, response);
            return;
        }
        if (request.getMethod().equals("POST")) {
            doPost(request, response);
            return;
        }
        response.setRedirectResponse("/405.html");
    }

    protected byte[] readFile(final String location) throws IOException {
        final String resourceName = STATIC_FILE_LOCATION + location;
        try (final InputStream fileStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (fileStream == null) {
                throw new NoSuchFileException(String.format("No Such file : location = %s", location));
            }
            return fileStream.readAllBytes();
        }
    }

    abstract void doGet(Http11Request request, Http11Response response) throws Exception;

    abstract void doPost(Http11Request request, Http11Response response) throws Exception;
}
