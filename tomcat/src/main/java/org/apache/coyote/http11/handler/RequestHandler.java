package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final ApiRouter apiRouter = new ApiRouter();

    public HttpResponse handleHttpRequest(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return new HttpResponse("500 Internal Server Error", "text/html;charset=utf-8", null);
        }

        if (httpRequest.pathEquals("") || httpRequest.pathEquals("/")) {
            return new HttpResponse("200 OK", "text/html;charset=utf-8", "Hello world!");
        }

        URL resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.getPath());
        if (resourceUrl == null) {
            return apiRouter.route(httpRequest);
        }
        return handleStaticFile(httpRequest, resourceUrl);
    }

    private HttpResponse handleStaticFile(HttpRequest httpRequest, URL resourceUrl) {
        try {
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()));
            if (httpRequest.getPath().endsWith(".css")) {
                return new HttpResponse("200 OK", "text/css;charset=utf-8", responseBody);
            }
            return new HttpResponse("200 OK", "text/html;charset=utf-8", responseBody);
        } catch (IOException | URISyntaxException exception) {
            logger.error(exception.getMessage(), exception);
            return new HttpResponse("404 Not Found", "text/html;charset=utf-8", null);
        }
    }
}
