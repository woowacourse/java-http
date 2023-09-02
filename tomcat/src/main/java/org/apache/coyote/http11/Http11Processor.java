package org.apache.coyote.http11;

import java.io.BufferedInputStream;
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
    private static final HttpResponseFactory RESPONSE_FACTORY = new HttpResponseFactory();

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
            final var bufferedInputStream = new BufferedInputStream(inputStream);
            final var inputStreamReader = new InputStreamReader(bufferedInputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            final HttpRequest httpRequest = parseRequest(bufferedReader);
            final HttpResponse httpResponse = RESPONSE_FACTORY.createHttpResponse(httpRequest);

            if (httpRequest.hasQueryString()) {
                final QueryStrings queryStrings = httpRequest.getQueryStrings();

                final User existUser = InMemoryUserRepository.findByAccount(queryStrings.getValueByName("account"))
                    .filter(user -> user.checkPassword(queryStrings.getValueByName("password")))
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

                LOGGER.info("user: {}", existUser);
            }

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final BufferedReader bufferedReader) {
        return HttpRequest.from(bufferedReader);
    }
}
