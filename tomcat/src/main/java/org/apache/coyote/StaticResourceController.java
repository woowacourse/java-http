package org.apache.coyote;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceController extends AbstractController {

    private static final String STATIC_DIRECTORY = "static";

    private static final String ROOT_PATH = "/";

    private static final String LOGIN_PATH = "/login";

    private static final String REGISTER_PATH = "/register";

    private static final byte[] HELLO_WORLD =
            "Hello world!".getBytes(StandardCharsets.UTF_8);

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response)
            throws IOException, URISyntaxException {
        serveResource(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response)
            throws IOException, URISyntaxException {

        serveResource(request, response);
    }

    private void serveResource(final HttpRequest request, final HttpResponse response)
            throws IOException, URISyntaxException {

        final String path = request.getPath();

        if (ROOT_PATH.equals(path)) {
            response.ok("text/html;charset=utf-8", HELLO_WORLD);
            return;
        }

        final String resourcePath = resolveResourcePath(path);

        final URL resource = getClass().getClassLoader().getResource(resourcePath);

        if (resource == null) {
            setNotFoundResponse(response);
            return;
        }

        final byte[] responseBody = Files.readAllBytes(Path.of(resource.toURI()));

        response.ok(resolveContentType(path), responseBody);
    }

    private void setNotFoundResponse(final HttpResponse response) {
        final byte[] responseBody = "Not Found".getBytes(StandardCharsets.UTF_8);

        response.notFound("text/plain;charset=utf-8", responseBody);
    }

    private String resolveResourcePath(final String path) {
        if (LOGIN_PATH.equals(path)) {
            return STATIC_DIRECTORY + "/login.html";
        }

        if (REGISTER_PATH.equals(path)) {
            return STATIC_DIRECTORY + "/register.html";
        }
        return STATIC_DIRECTORY + path;
    }

    private String resolveContentType(final String path) {

        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html;charset=utf-8";
    }
}