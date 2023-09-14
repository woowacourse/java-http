package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.catalina.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

abstract class AbstractController implements Controller {

    protected static final String DEFAULT_FILE_LOCATION = "static/";

    public abstract boolean canHandle(HttpRequest request);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        // http method 분기문
    }

    protected String readResponseBody(final String requestUri) throws IOException, URISyntaxException {
        final URL url = getClass().getClassLoader().getResource(DEFAULT_FILE_LOCATION + requestUri);
        if (url == null) {
            URL notFoundUrl = getClass().getClassLoader().getResource(DEFAULT_FILE_LOCATION + "404.html");
            final var path = Paths.get(notFoundUrl.toURI());
            return Files.readString(path);
        }
        final var path = Paths.get(url.toURI());
        return Files.readString(path);
    }
}
