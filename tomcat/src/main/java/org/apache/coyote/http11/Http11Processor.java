package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
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

    private HttpResponse createResponse(final HttpRequest request) {
        Optional<Controller> controllerOptional = findController(request);

        if (controllerOptional.isPresent()) {
            Controller controller = controllerOptional.get();
            return controller.doService(request);
        }

        return createStaticResourceResponse(request);
    }

    private Optional<Controller> findController(final HttpRequest request) {
        if (request.getUriPath().equals("/")) {
            return Optional.of(new HomeController());
        }

        if (request.getUriPath().equals("/login")) {
            return Optional.of(new LoginController());
        }

        if (request.getUriPath().equals("/register")) {
            return Optional.of(new RegisterController());
        }

        return Optional.empty();
    }

    private HttpResponse createStaticResourceResponse(final HttpRequest request) {
        if (hasStaticResourceFile(request.getUriPath())) {
            return HttpResponse.ok().fileBody(request.getUriPath()).build();
        }
        return HttpResponse.notFound().build();
    }

    private boolean hasStaticResourceFile(final String uri) {
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return resource != null && new File(resource.getFile()).isFile();
    }

    private void addContentTypeHeader(final HttpRequest request, final HttpResponse response) {
        if (response.hasBody()) {
            response.addHeader("Content-Type", new ContentTypeExtractor().extract(request).getValue());
        }
    }
}
