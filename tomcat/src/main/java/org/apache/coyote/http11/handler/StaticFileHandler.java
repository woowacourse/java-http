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

public class StaticFileHandler {

    private static final Logger logger = LoggerFactory.getLogger(StaticFileHandler.class);
    public static final String DEFAULT_EXTENSION_OF_STATIC_FILE = ".html";

    public static HttpResponse handle(HttpRequest httpRequest, URL resourceUrl) {
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

    public static HttpResponse handleDefault(String viewName) {
        try {
            URL resourceUrl = StaticFileHandler.class.getClassLoader().getResource("static/" + viewName + DEFAULT_EXTENSION_OF_STATIC_FILE);
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()));
            return new HttpResponse("200 OK", "text/html;charset=utf-8", responseBody);
        } catch (IOException | URISyntaxException exception) {
            logger.error(exception.getMessage(), exception);
            return new HttpResponse("404 Not Found", "text/html;charset=utf-8", null);
        }
    }
}
