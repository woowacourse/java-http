package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String uri = bufferedReader.readLine().split(" ")[1];
            ResponseProcessor responseProcessor = new ResponseProcessor(uri);

            if (responseProcessor.existQueryParameter()) {
                checkUser(responseProcessor.getQueryParameterMap());
            }

            final var response = responseProcessor.getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
            bufferedReader.close();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkUser(Map<String, String> queryParameterMap) {
        String account = queryParameterMap.get("account");
        String password = queryParameterMap.get("password");
        if (account == null || password == null) {
            return;
        }

        User user = InMemoryUserRepository.findByAccount(account).orElseThrow(UserNotFoundException::new);
        if (!user.checkPassword(password)) {
            throw new AuthenticationException();
        }
        System.out.println(user);
    }
}
