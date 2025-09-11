package org.apache.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

public class StaticController implements Controller {

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".html", ".css", ".js", ".ico");

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return ALLOWED_EXTENSIONS.stream().anyMatch(path::endsWith);
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException, URISyntaxException {
        final ClassLoader classLoader = getClass().getClassLoader();
        System.out.println(httpRequest.getStaticFilePath());
        final URL url = classLoader.getResource(httpRequest.getStaticFilePath());

        if (url == null) {
            return HttpResponse.notFound(httpRequest);
        }

        final File resourceFile = new File(Objects.requireNonNull(url).toURI());
        final Path path = resourceFile.toPath();

        httpResponse.setResponseBody(new String(Files.readAllBytes(path)));
        httpResponse.setHttpStatus(HttpStatus.OK);

        return httpResponse;
    }
}
