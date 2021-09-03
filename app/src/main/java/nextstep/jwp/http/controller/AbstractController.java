package nextstep.jwp.http.controller;

import nextstep.jwp.exception.NotFoundResourceException;
import nextstep.jwp.exception.NotSupportedMethodException;
import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractController implements Controller {

    protected static final String RESOURCE_PREFIX = "static";

    @Override
    public JwpHttpResponse handle(JwpHttpRequest request) throws IOException {
        if (request.isGetRequest()) {
            return doGet(request);
        }

        if (request.isPostRequest()) {
            return doPost(request);
        }

        throw new NotSupportedMethodException();
    }

    @Override
    public JwpHttpResponse doGet(JwpHttpRequest request) throws IOException {
        return JwpHttpResponse.notFound();
    }

    @Override
    public JwpHttpResponse doPost(JwpHttpRequest request) {
        return JwpHttpResponse.notFound();
    }

    protected String findResourceFile(String uri) throws IOException {
        try {
            URL resource = getClass().getClassLoader().getResource(uri);
            final Path path;
            path = Paths.get(resource.toURI());
            return new String(Files.readAllBytes(path));
        } catch (URISyntaxException | NullPointerException e) {
            throw new NotFoundResourceException(uri);
        }
    }
}
