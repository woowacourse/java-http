package org.apache.coyote.http11;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String NEW_LINE = "\r\n";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
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
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream();
             var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String request = readRequest(bufferedReader);
            if (request.isBlank()) {
                return;
            }

            HttpRequest httpRequest = HttpRequest.from(request);
            HttpResponse httpResponse = HttpResponse.from(httpRequest.getFileSource());

            String response = httpResponse.getResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
            showUserIfNecessary(httpRequest);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequest(BufferedReader bufferedReader) throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            requestBuilder.append(bufferedReader.readLine())
                    .append(NEW_LINE);
        }
        return requestBuilder.toString();
    }

    private void showUserIfNecessary(HttpRequest httpRequest) {
        if (httpRequest.isLoginRequest()) {
            showUser(httpRequest.getQueryParamValueOf(ACCOUNT_KEY), httpRequest.getQueryParamValueOf(PASSWORD_KEY));
        }
    }

    private void showUser(String account, String password) {
        User user = findUser(account, password);
        log.debug(user.toString());
    }

    private User findUser(String account, String password) {
        User user = findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException();
        }
        return user;
    }
}
