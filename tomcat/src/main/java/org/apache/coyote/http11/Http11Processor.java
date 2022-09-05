package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_RESOURCE = "/login.html";

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
            final Http11URI urlPath = Http11URI.of(inputStream);
            final Http11StaticFile staticFile = Http11StaticFile.of(urlPath);
            final Http11Response http11Response = new Http11Response(outputStream);
            logLogin(urlPath);
            http11Response.write(staticFile);
        } catch (IOException | UncheckedServletException | URISyntaxException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void logLogin(final Http11URI urlPath) {
        if (urlPath.hasPath(LOGIN_RESOURCE) && loginSuccess(urlPath)) {
            final User loggedInUser = findUser(urlPath);
            log.info(loggedInUser.toString());
        }
    }

    private boolean loginSuccess(final Http11URI urlPath) {
        if (!urlPath.hasParams()) {
            return false;
        }
        final User foundUser = findUser(urlPath);
        final String password = urlPath.findParamByKey("password");
        return foundUser.checkPassword(password);

    }

    private User findUser(final Http11URI urlPath) {
        String account = urlPath.findParamByKey("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
    }
}
