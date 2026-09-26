package org.apache.coyote.controller;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.resource.ResourceReader;


public class StaticResourceController extends AbstractController {

    private static final String STATIC_DIRECTORY = "static";

    private static final String ROOT_PATH = "/";

    private static final byte[] HELLO_WORLD =
            "Hello world!".getBytes(StandardCharsets.UTF_8);

    private final ResourceReader resourceReader;

    public StaticResourceController(final ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response)
            throws IOException {
        serveResource(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response)
            throws IOException {

        serveResource(request, response);
    }

    private void serveResource(final HttpRequest request, final HttpResponse response)
            throws IOException {

        final String path = response.getForwardPath()
                .orElse(request.getPath());

        if (ROOT_PATH.equals(path)) {
            response.ok("text/html;charset=utf-8", HELLO_WORLD);
            return;
        }

        final Optional<byte[]> resource =
                resourceReader.read(
                        STATIC_DIRECTORY + path
                );

        if (resource.isEmpty()) {
            setNotFoundResponse(response);
            return;
        }


        response.ok(resolveContentType(path), resource.get());
    }

    private void setNotFoundResponse(final HttpResponse response) {
        final byte[] responseBody = "Not Found".getBytes(StandardCharsets.UTF_8);

        response.notFound("text/plain;charset=utf-8", responseBody);
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