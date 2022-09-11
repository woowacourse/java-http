package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) throws IOException {
        if (request.isSameMethod(HttpMethod.POST)) {
            return doPost(request);
        }
        return doGet(request);
    }

    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        return null;
    }

    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        return null;
    }

    protected String readFile(final String path) throws IOException {
        final String filePath = String.format("static%s", path);
        final URL resource = this.getClass().getClassLoader().getResource(filePath);
        return Files.readString(Path.of(Objects.requireNonNull(resource).getPath()));
    }
}
