package org.apache.coyote.http11;

import static nextstep.jwp.Url.LOGIN;

import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.Url;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final Http11Request request = Http11Request.of(inputStream);
            final String url = request.getRequestUrl();
            final Http11Response response = Url.getResponseFrom(url);
            if (Url.find(url).equals(LOGIN)) {
                Http11QueryParams queryParams = Http11QueryParams.from(url);
                logUserInfo(queryParams);
            }
            response.write(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void logUserInfo(Http11QueryParams queryParams) {
        final String account = queryParams.getValueFrom("account");
        final String password = queryParams.getValueFrom("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }
}
