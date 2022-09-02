package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestBody;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
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
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest httpRequest = HttpRequest.of(bufferedReader.readLine(), HttpHeader.from(bufferedReader));
            final HttpRequestBody requestBody = HttpRequestBody.from(bufferedReader, httpRequest.getContentLength());

            HttpResponse httpResponse = HttpResponse.fromHttpRequest(httpRequest);

            if (httpRequest.isRegister()) {
                httpResponse = register(requestBody, httpResponse);
            }

            if (httpRequest.isLogin()) {
                httpResponse = login(requestBody, httpResponse);
            }

            if (httpRequest.isLoginPage() && httpRequest.alreadyLogin()) {
                httpResponse = httpResponse.changeStatusCode(HttpStatusCode.FOUND)
                        .setLocationAsHome();
            }

            outputStream.write(httpResponse.toResponseBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse login(final HttpRequestBody requestBody, final HttpResponse httpResponse) {
        final Optional<User> possibleUser = InMemoryUserRepository.findByAccount(requestBody.get("account"));
        if (possibleUser.isEmpty()) {
            return httpResponse;
        }

        final UUID sessionId = UUID.randomUUID();
        final HttpSession session = new Session(String.valueOf(sessionId));
        session.setAttribute("user", possibleUser.get());
        new SessionManager().add(session);

        return httpResponse.changeStatusCode(HttpStatusCode.FOUND)
                .setLocationAsHome()
                .setSessionId(sessionId);
    }

    private HttpResponse register(final HttpRequestBody requestBody, final HttpResponse httpResponse) {
        final User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        final UUID sessionId = UUID.randomUUID();
        final HttpSession session = new Session(String.valueOf(sessionId));
        session.setAttribute("user", user);
        new SessionManager().add(session);

        return httpResponse.changeStatusCode(HttpStatusCode.FOUND)
                .setLocationAsHome()
                .setSessionId(sessionId);
    }
}
