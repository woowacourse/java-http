package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_TYPE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class UnsupportedResourceHandler implements ResourceHandler {

    private static final String NOT_FOUND_PAGE_PATH = "/404.html";
    private static final String RESOURCE_DIRECTORY = "static";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return false;
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        String fileName = RESOURCE_DIRECTORY + NOT_FOUND_PAGE_PATH;
        URL resource = getClass().getClassLoader().getResource(fileName);
        final Path path = new File(resource.getPath()).toPath();
        final String body = new String(Files.readAllBytes(path));
        final HttpResponse httpResponse = HttpResponse.of(StatusCode.BAD_REQUEST, body);
        httpResponse.addHeader(CONTENT_TYPE, ContentType.HTML.getContentType());
        httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return httpResponse;
    }
}
