package org.apache.web;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourcesController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(StaticResourcesController.class);

    @Override
    public Http11Response control(final Http11Request request) {
        try {
            String path = request.extractStaticPath();
            if (path.equals("/")) {
                return Http11Response.ok("text/html;charset=utf-8", "Hello world!");
            }

            URL url = getURL(path);
            if (url == null) {
                String notFound = Files.readString(Paths.get(getURL("static/404.html").toURI()));
                return Http11Response.notFound("text/html;charset=utf-8", notFound);
            }

            String body = Files.readString(Paths.get(url.toURI()));
            String contentType = extractContentType(path);

            return Http11Response.ok(contentType, body);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
            return Http11Response.serverError();
        }
    }

    private URL getURL(final String path) {
        return getClass().getClassLoader().getResource(path);
    }

    private String extractContentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }
}
