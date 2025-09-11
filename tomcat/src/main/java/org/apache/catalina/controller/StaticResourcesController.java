package org.apache.catalina.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.catalina.resolver.StaticResourceResolver;
import org.apache.catalina.resolver.StaticResourceResolver.ResolvedResource;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourcesController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(StaticResourcesController.class);

    private final StaticResourceResolver resolver = new StaticResourceResolver();

    @Override
    public Http11Response doGet(final Http11Request request) {
        try {
            String uri = request.getUri();
            ResolvedResource resource = resolver.resolve(uri);
            String body = Files.readString(Paths.get(resource.url().toURI()));
            String contentType = resource.contentType();

            return Http11Response.ok(contentType, body);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
            return Http11Response.serverError();
        }
    }
}
