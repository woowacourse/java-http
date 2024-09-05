package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            Http11Request request = RequestExtractor.extract(inputStream);
            execute(request);
            Http11Response response = ResponseBuilder.build(request);

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void execute(Http11Request request) {
        String path = request.getPath();
        Map<String, String> queryParams = request.getQueryParams();
        log.info("request path = {}", path);
        if (path.equals("/login")) {
            String userAccount = queryParams.get("account");
            String userPassword = queryParams.get("password");
            Optional<User> rawUser = InMemoryUserRepository.findByAccount(userAccount);
            rawUser.ifPresent(user -> {
                if (user.checkPassword(userPassword)) {
                    log.info("user: {}", user);
                }
            });
        }
    }
}
