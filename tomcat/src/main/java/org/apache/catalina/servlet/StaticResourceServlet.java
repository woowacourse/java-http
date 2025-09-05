package org.apache.catalina.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestPath;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseGenerator;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class StaticResourceServlet extends HttpServlet {

    private static final String STATIC_RECOURSE_PATH = "static";

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        RequestPath requestPath = httpRequest.getRequestPath();

        URL resourceUrl = StaticResourceServlet.class.getClassLoader()
                .getResource(STATIC_RECOURSE_PATH + requestPath.getRequestPath());
        return resourceUrl != null;
    }

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest) {
        String resource = findResource(httpRequest.getRequestPath().getRequestPath()); //TODO: get열차 칙칙폭폭..
        Optional<ContentType> contentType = findResourceExtension(httpRequest.getRequestLine());

        return contentType.map(type -> HttpResponseGenerator.generate(resource, type, HttpStatus.OK))
                .orElseGet(() -> HttpResponseGenerator.generate(resource, null, HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponseGenerator.generate("", ContentType.HTML, HttpStatus.METHOD_NOT_ALLOWED);
    }

    private Optional<ContentType> findResourceExtension(final RequestLine requestLine) {
        String extension = requestLine.getRequestPathExtension();
        return ContentType.findContentType(extension);
    }

    private String findResource(final String requestPath) {
        URL resourceUrl = StaticResourceServlet.class.getClassLoader().getResource(STATIC_RECOURSE_PATH + requestPath);

        try {
            Path filePath = Path.of(resourceUrl.toURI());

            return Files.readString(filePath);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage());
        }
    }
}
