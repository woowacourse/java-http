package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

public class StaticController implements Controller{

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    private static final String STATIC_DIRECTORY = "static";
    private static final String DELIMITER = "\r\n";
    private static final String INDEX_URI = "/";


    @Override
    public void handle(HttpRequest request, OutputStream outputStream) {
        if (request.getUri().equals(INDEX_URI)) {
            request = HttpRequest.toIndex();
        }
        File file = readStaticFile(request);
        try {
            if (file == null) {
                request = HttpRequest.toNotFound();
                file = readStaticFile(request);
            }
            final String response = makeResponse(new String(Files.readAllBytes(file.toPath())), request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File readStaticFile(HttpRequest request) {
        URL resource = classLoader.getResource(STATIC_DIRECTORY + request.getUri());
        if (resource == null) {
            return null;
        }
        return new File(resource.getFile());
    }

    private String makeResponse(final String responseBody, final HttpRequest httpRequest) {
        return String.join(DELIMITER,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + httpRequest.getExtension() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
