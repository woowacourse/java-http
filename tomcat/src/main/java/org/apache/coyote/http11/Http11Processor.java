package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                req -> req.method().equals("GET") && req.path().equals("/login"), this::processLoginPage,
                req -> req.method().equals("GET") && req.path().equals("/register"), this::processRegisterPage
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
            Http11Request request = Http11Request.parse(requestLines);
            log.debug(request.toString());

            for (final var processor : processors.entrySet()) {
                if (processor.getKey().test(request)) {
                    Http11Response response = processor.getValue().apply(request);
                    log.debug(response.toString());
                    outputStream.write(response.toMessage());
                    outputStream.flush();
                    return;
                }
            }

            Http11Response response = defaultProcessor.apply(request);
            log.debug(response.toString());
            outputStream.write(response.toMessage());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Response processRootPage(Http11Request request) {
        return Http11Response.builder(200)
                .addHeader("Content-Type", "text/html;charset=utf-8")
                .body("Hello world!".getBytes())
                .build();
    }

    private Http11Response processLoginPage(Http11Request request) {
        if (request.parameters().containsKey("account") && request.parameters().containsKey("password")) {
            String account = request.parameters().get("account");
            String password = request.parameters().get("password");

            Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
            if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
                return Http11Response.builder(302)
                        .location("/index.html")
                        .build();
            }

            return Http11Response.builder(302)
                    .location("/401.html")
                    .build();
        }

        return processStaticResource(new Http11Request(
                request.method(),
                "login.html",
                request.parameters(),
                request.protocolVersion()
        ));
    }

    private Http11Response processRegisterPage(Http11Request request) {
        return processStaticResource(new Http11Request(
                request.method(),
                "register.html",
                request.parameters(),
                request.protocolVersion()
        ));
    }

    private Http11Response processStaticResource(Http11Request request) {
        String contentType = URLConnection.guessContentTypeFromName(request.path());
        final byte[] responseBody;
        try {
            responseBody = staticResourceReader.read(request.path());
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
