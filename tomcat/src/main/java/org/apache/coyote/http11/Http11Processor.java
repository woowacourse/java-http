package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.HttpRequest;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

import static nextstep.jwp.utils.RequestUtil.getResponseBody;

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

            final var responseBody = getResponseBody(httpRequest.getPath(), this.getClass());
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + httpRequest.getContentType() + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void logParams(final HttpRequest httpRequest) {
        final Map<String, String> params = httpRequest.getParams();
        if (!params.isEmpty() && checkUser(params)) {
            log.info("request uri : {}. query param : {}", httpRequest.getPath(), params);
        }
    }

    private boolean checkUser(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        return optionalUser.filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
