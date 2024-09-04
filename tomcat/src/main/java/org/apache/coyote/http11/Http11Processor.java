package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final StaticResourceReader staticResourceReader;
    private final Function<Http11Request, Http11Response> defaultProcessor;
    private final Map<Predicate<Http11Request>, Function<Http11Request, Http11Response>> processors;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.staticResourceReader = new StaticResourceReader();
        this.defaultProcessor = this::processStaticResource;
        this.processors = Map.of(
                req -> req.method().equals("GET") && req.path().equals("/"), this::processRootPage,
                req -> req.method().equals("GET") && req.path().equals("/login"), this::processLoginPage
        );
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            List<String> requestLines = Http11InputStreamReader.read(inputStream);
            Http11Request servletRequest = Http11Request.parse(requestLines);
            log.debug(servletRequest.toString());

            for (final var processor : processors.entrySet()) {
                if (processor.getKey().test(servletRequest)) {
                    Http11Response response = processor.getValue().apply(servletRequest);
                    outputStream.write(response.toMessage());
                    outputStream.flush();
                    return;
                }
            }

            Http11Response response = defaultProcessor.apply(servletRequest);
            outputStream.write(response.toMessage());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Response processRootPage(Http11Request servletRequest) {
        return Http11Response.builder(200)
                .addHeader("Content-Type", "text/html;charset=utf-8")
                .body("Hello world!".getBytes())
                .build();
    }

    private Http11Response processLoginPage(Http11Request servletRequest) {
        return processStaticResource(new Http11Request(
                servletRequest.method(),
                "login.html",
                servletRequest.parameters(),
                servletRequest.protocolVersion()
        ));
    }

    private Http11Response processStaticResource(Http11Request servletRequest) {
        String contentType = URLConnection.guessContentTypeFromName(servletRequest.path());
        final byte[] responseBody;
        try {
            responseBody = staticResourceReader.read(servletRequest.path());
            if (responseBody == null) {
                return Http11Response.builder(404)
                        .build();
            }
            return Http11Response.builder(200)
                    .contentType(contentType)
                    .body(responseBody)
                    .build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Http11Response.builder(500)
                    .build();
        }
    }
}
