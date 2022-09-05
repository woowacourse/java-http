package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.exception.UncheckedServletException;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.HandlerMapper;
import org.apache.coyote.model.HttpParam;
import org.apache.coyote.model.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            HttpRequest httpRequest = HttpRequest.of(reader.readLine());
            logParams(httpRequest);

            final var response = createResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(HttpRequest httpRequest) {
        Handler handler = HandlerMapper.findHandler(httpRequest);
        return handler.getResponse();
    }

    private void logParams(final HttpRequest httpRequest) {
        HttpParam httpParam = httpRequest.getParams();
        if (!httpParam.isEmpty() && checkUser(httpParam)) {
            log.info("request uri : {}. query param : {}", httpRequest.getPath(), httpParam);
        }
    }

    private boolean checkUser(final HttpParam params) {
        final String account = params.getByKey("account");
        final String password = params.getByKey("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        return optionalUser.filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
