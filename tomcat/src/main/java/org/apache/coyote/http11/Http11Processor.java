package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.StaticResourceController;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestConvertor httpRequestConvertor = new HttpRequestConvertor();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = httpRequestConvertor.convert(inputStream);
            final HttpResponse response = createResponse(request);
            addContentTypeHeader(request, response);

            outputStream.write(response.writeValueAsBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(final HttpRequest request) throws IOException {
        if (request.getUriPath().equals("/")) {
            return new HomeController().handle(request);
        }

        if (request.getUriPath().equals("/login")) {
            return new LoginController().handle(request);
        }

        if (hasStaticResourceFile(request.getUriPath())) {
            return new StaticResourceController().handle(request);
        }

        return HttpResponse.notFound().build();
    }

    private boolean hasStaticResourceFile(final String uri) {
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return resource != null && new File(resource.getFile()).isFile();
    }

    private void addContentTypeHeader(final HttpRequest request, final HttpResponse response) {
        if (response.hasBody()) {
            response.addHeader("Content-Type", new ContentTypeExtractor().extract(request));
        }
    }
}
