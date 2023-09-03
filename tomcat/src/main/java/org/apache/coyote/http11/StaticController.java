package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticController implements Controller {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    private static final String STATIC_DIRECTORY = "static";
    private static final String INDEX_URI = "/";

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.getUri().equals(INDEX_URI)) {
            request = HttpRequest.toIndex();
        }
        File file = readStaticFile(request);
        try {
            return new HttpResponse(StatusCode.OK, "text/" + request.getExtension(),
                    new String(Files.readAllBytes(file.toPath())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File readStaticFile(HttpRequest request) {
        URL resource = classLoader.getResource(STATIC_DIRECTORY + request.getUri());
        return new File(resource.getFile());
    }
}
