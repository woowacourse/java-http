package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream();
                final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return;
            }

            RequestLine requestLine = RequestLine.from(firstLine);
            RequestHandler requestHandler = new RequestHandler(requestLine);

            if (requestLine.isQueryStringExisted()) {
                logUser(requestLine);
            }

            HttpResponse httpResponse = requestHandler.extractHttpResponse();
            String response = httpResponse.extractResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void logUser(RequestLine requestLine) {
        String account = requestLine.findQueryStringValue("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

        if (user.checkPassword(requestLine.findQueryStringValue("password"))) {
            log.info("User: {}", user);
        }
    }


}

