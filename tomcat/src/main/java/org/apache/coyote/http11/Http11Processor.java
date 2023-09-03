package org.apache.coyote.http11;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        LOGGER.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            HttpRequest request = HttpRequest.from(reader);

            if (request.getPath().equals("/login") && request.hasQueryStrings()) {
                final User user = InMemoryUserRepository.findByAccount(request.getQueryString("account"))
                                                        .filter(u -> u.checkPassword(request.getQueryString("password")))
                                                        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));
                LOGGER.info("login success: {}", user);
            }

            HttpResponse response = HttpResponse.from(request);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
