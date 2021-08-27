package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import nextstep.jwp.httpserver.domain.response.HttpResponse;

public abstract class AbstractHandlerAdapter implements HandlerAdapter {

    protected List<String> readFile(String resourcePath) throws URISyntaxException, IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("static" + resourcePath + ".html");
        final Path path = Paths.get(url.toURI());
        return Files.readAllLines(path);
    }

    protected abstract String getResourcePath(String requestUri);

    protected abstract String getResponse(HttpResponse httpResponse, List<String> body);
}
