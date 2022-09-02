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

            final Http11URLPath urlPath = Http11URLPath.of(inputStream);
            final HttP11StaticFile staticFile = HttP11StaticFile.of(urlPath);
            final Http11Response http11Response = new Http11Response(outputStream);
            logLogin(urlPath);
            http11Response.write(staticFile);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void logLogin(final Http11URLPath urlPath) {
        if (urlPath.hasPath("/login.html") && urlPath.hasParams()) {
            String id = urlPath.findParamByKey("account");
            String password = urlPath.findParamByKey("password");
            final User foundAccount = InMemoryUserRepository.findByAccount(id)
                    .orElseThrow(IllegalArgumentException::new);
            if (foundAccount.checkPassword(password)) {
                log.info(foundAccount.toString());
            }
        }
    }
}
