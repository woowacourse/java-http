package nextstep.jwp.controller;

import nextstep.jwp.ResourceNotFoundException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public abstract class Controller {
    public HttpResponse doService(final HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return doGet(httpRequest);
        }
        return doPost(httpRequest);
    }

    protected HttpResponse doGet(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();
        final String responseBody = readFile(path);

        return new HttpResponse(
                httpRequest.getProtocol(),
                HttpStatus.OK,
                "text/html",
                responseBody.getBytes().length,
                responseBody);
    }

    protected String readFile(final String path) {
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

    public abstract boolean canHandle(final HttpRequest httpRequest);

    public abstract HttpResponse doPost(final HttpRequest httpRequest);
}
