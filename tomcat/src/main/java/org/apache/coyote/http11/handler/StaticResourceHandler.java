package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeader.CONTENT_TYPE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class StaticResourceHandler implements ResourceHandler {

    private static final String NOT_FOUND_PAGE_PATH = "/404.html";
    private static final String RESOURCE_DIRECTORY = "static";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return HttpMethod.GET == httpRequest.getHttpMethod();
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        String fileName = RESOURCE_DIRECTORY + httpRequest.getPath();
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            fileName = RESOURCE_DIRECTORY + NOT_FOUND_PAGE_PATH;
            resource = getClass().getClassLoader().getResource(fileName);
            return createHttpResponse(StatusCode.BAD_REQUEST, resource, ContentType.from(fileName).getContentType());
        }
        return createHttpResponse(StatusCode.OK, resource, ContentType.from(fileName).getContentType());
    }

    private HttpResponse createHttpResponse(final StatusCode statusCode, final URL resource, final String contentType)
            throws IOException {
        final Path path = new File(resource.getPath()).toPath();
        final String body = new String(Files.readAllBytes(path));
        final HttpResponse httpResponse = new HttpResponse(statusCode, body);
        httpResponse.addHeader(CONTENT_TYPE, contentType);
        httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return httpResponse;
    }
}
