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
                req -> req.method().equals("GET") && req.path().equals("/register"), this::processRegisterPage,
                req -> req.method().equals("POST") && req.path().equals("/register"), this::processRegister
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
        return Http11Response.builder(Status.OK)
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
                log.info("로그인 성공! - 아이디 : " + optionalUser.get().getAccount());
                return Http11Response.builder(Status.FOUND)
                        .location("/index.html")
                        .build();
            }

            return Http11Response.builder(Status.FOUND)
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

    private Http11Response processRegister(Http11Request request) {
        Map<String, String> body = Http11Request.extractParameters(request.body());

        if (!body.containsKey("account") ||
                !body.containsKey("password") ||
                !body.containsKey("email")) {
            return Http11Response.builder(Status.BAD_REQUEST)
                    .build();
        }

        String account = body.get("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return Http11Response.builder(Status.CONFLICT)
                    .build();
        }

        InMemoryUserRepository.save(new User(account, body.get("password"), body.get("email")));
        return Http11Response.builder(Status.FOUND)
                .location("/index.html")
                .build();
    }

    private Http11Response processStaticResource(Http11Request request) {
        String contentType = URLConnection.guessContentTypeFromName(request.path());
        final byte[] responseBody;
        try {
            responseBody = staticResourceReader.read(request.path());
            if (responseBody == null) {
                return Http11Response.builder(Status.NOT_FOUND)
                        .build();
            }
            return Http11Response.builder(Status.OK)
                    .contentType(contentType)
                    .body(responseBody)
                    .build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Http11Response.builder(Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
