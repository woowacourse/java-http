package nextstep.jwp.framework.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import nextstep.jwp.framework.infrastructure.exception.NotFoundException;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public abstract class AbstractController implements Controller {

    public HttpResponse doService(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getMethod();
        if (httpMethod.equals(HttpMethod.GET)) {
            return doGet(httpRequest);
        }
        return doPost(httpRequest);
    }

    protected String readFile(String url) {
        String resourcePath = "static" + url;
        URL resource = Thread.currentThread().getContextClassLoader()
            .getResource(resourcePath);
        try {
            Path path = Paths.get(resource.toURI());
            return String.join("\r\n", Files.readAllLines(path));
        } catch (IOException | URISyntaxException exception) {
            throw new NotFoundException(HttpStatus.NOT_FOUND);
        }
    }

    protected abstract HttpResponse doGet(HttpRequest httpRequest);

    protected abstract HttpResponse doPost(HttpRequest httpRequest);
}
