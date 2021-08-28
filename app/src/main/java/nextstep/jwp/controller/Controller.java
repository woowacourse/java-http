package nextstep.jwp.controller;

import nextstep.jwp.ResourceNotFoundException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public interface Controller {
    boolean canHandle(final HttpRequest httpRequest);

    HttpResponse doService(final HttpRequest httpRequest);

    default String readFile(final String path) {
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource("static" + path);
        try {
            final File file = new File(Objects.requireNonNull(url).getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new ResourceNotFoundException();
        }
    }
}
