package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.general.HttpProtocolVersion;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.apache.coyote.http11.httpResponse.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFileHandler {

    private static final Logger logger = LoggerFactory.getLogger(StaticFileHandler.class);
    public static final String DEFAULT_EXTENSION_OF_STATIC_FILE = ".html";

    public static HttpResponse handle(HttpRequest httpRequest, URL resourceUrl) {
        try {
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()), StandardCharsets.UTF_8);
            if (httpRequest.getPath().endsWith(".css")) {
                return new HttpResponse(ContentType.TEXT_CSS, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.OK), responseBody);
            }
            return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.OK), responseBody);
        } catch (IOException | URISyntaxException | NullPointerException exception) {
            logger.error(exception.getMessage(), exception);
            return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.NOT_FOUND), null);
        }
    }

    public static HttpResponse handleDefault(HttpProtocolVersion protocolVersion, HttpStatus status, String viewName) {
        try {
            URL resourceUrl = StaticFileHandler.class.getClassLoader().getResource("static/" + viewName + DEFAULT_EXTENSION_OF_STATIC_FILE);
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()));
            return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(protocolVersion, status), responseBody);
        } catch (IOException | URISyntaxException | NullPointerException exception) {
            logger.error(exception.getMessage(), exception);
            return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(protocolVersion, HttpStatus.NOT_FOUND), null);
        }
    }
}
