package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.exception.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public abstract class AbstractController implements Controller {
    private static final String DEFAULT_PATH = "static";

    public HttpResponse doService(final HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return doGet(httpRequest);
        }
        return doPost(httpRequest);
    }

    public String readFile(final String path) {
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(DEFAULT_PATH + path);
        try {
            final File file = new File(Objects.requireNonNull(url).getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException | RuntimeException exception) {
            throw new ResourceNotFoundException();
        }
    }

    public abstract boolean canHandle(final HttpRequest httpRequest);

    public abstract HttpResponse doGet(final HttpRequest httpRequest);

    public abstract HttpResponse doPost(final HttpRequest httpRequest);
}
