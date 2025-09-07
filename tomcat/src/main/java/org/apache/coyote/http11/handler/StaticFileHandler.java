package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFileHandler {

    private static final Logger logger = LoggerFactory.getLogger(StaticFileHandler.class);
    public static final String DEFAULT_EXTENSION_OF_STATIC_FILE = ".html";

    public static HttpResponse handle(HttpRequest httpRequest, URL resourceUrl) {
        try {
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()));
            if (httpRequest.getPath().endsWith(".css")) {
                return new HttpResponse(HttpStatus.OK, ContentType.TEXT_CSS, responseBody);
            }
            return new HttpResponse(HttpStatus.OK, ContentType.TEXT_HTML, responseBody);
        } catch (IOException | URISyntaxException exception) {
            logger.error(exception.getMessage(), exception);
            return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.TEXT_HTML, null);
        }
    }

    public static HttpResponse handleDefault(String viewName) {
        try {
            URL resourceUrl = StaticFileHandler.class.getClassLoader().getResource("static/" + viewName + DEFAULT_EXTENSION_OF_STATIC_FILE);
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()));
            return new HttpResponse(HttpStatus.OK, ContentType.TEXT_HTML, responseBody);
        } catch (IOException | URISyntaxException exception) {
            logger.error(exception.getMessage(), exception);
            return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.TEXT_HTML, null);
        }
    }
}
