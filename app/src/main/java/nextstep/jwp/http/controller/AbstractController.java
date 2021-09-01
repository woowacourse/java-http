package nextstep.jwp.http.controller;

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
    public JwpHttpResponse handle(JwpHttpRequest request) throws URISyntaxException, IOException {
        if (request.isGetRequest()) {
            return doGet(request);
        }

        if (request.isPostRequest()) {
            return doPost(request);
        }

        throw new NotSupportedMethodException();
    }

    @Override
    public JwpHttpResponse doGet(JwpHttpRequest request) throws URISyntaxException, IOException {
        return JwpHttpResponse.notFound();
    }

    @Override
    public JwpHttpResponse doPost(JwpHttpRequest request) {
        return JwpHttpResponse.notFound();
    }

    protected String findResourceFile(String uri) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(uri);
        final Path path = Paths.get(resource.toURI());
        return new String(Files.readAllBytes(path));
    }
}
