package kokodak;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class NotFoundHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final String fileName = "static/404.html";
        final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
        final Path path = new File(resourceUrl.getPath()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));
        return HttpResponse.builder()
                           .header("Content-Type", "text/html;charset=utf-8")
                           .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                           .body(responseBody)
                           .build();
    }
}
