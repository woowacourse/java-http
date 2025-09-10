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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFileHandler {

    private static final Logger logger = LoggerFactory.getLogger(StaticFileHandler.class);
    public static final String DEFAULT_EXTENSION_OF_STATIC_FILE = ".html";

    public static HttpResponse handle(HttpRequest httpRequest) {
        URL resourceUrl = StaticFileHandler.class.getClassLoader().getResource("static" + httpRequest.getPath());
        if (httpRequest.getPath().endsWith(".css")) {
            return buildHttpResponse(resourceUrl, httpRequest.getProtocolVersion(), ContentType.TEXT_CSS);
        }
        return buildHttpResponse(resourceUrl, httpRequest.getProtocolVersion(), ContentType.TEXT_HTML);
    }

    private static HttpResponse buildHttpResponse(URL resourceUrl, HttpProtocolVersion protocolVersion, ContentType contentType) {
        try {
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()), StandardCharsets.UTF_8);
            return HttpResponse.of(protocolVersion, HttpStatus.OK, contentType, responseBody);
        } catch (IOException | URISyntaxException | NullPointerException exception) {
            logger.error(exception.getMessage(), exception);
            return HttpResponse.of(protocolVersion, HttpStatus.NOT_FOUND, ContentType.TEXT_HTML, "존재하지 않습니다.");
        }
    }

    public static HttpResponse handleDefault(HttpProtocolVersion protocolVersion, String viewName) {
        URL resourceUrl = StaticFileHandler.class.getClassLoader().getResource("static/" + viewName + DEFAULT_EXTENSION_OF_STATIC_FILE);
        return buildHttpResponse(resourceUrl, protocolVersion, ContentType.TEXT_HTML);
    }
}
