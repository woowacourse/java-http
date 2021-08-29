package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            );

            HttpResponse httpResponse = new HttpResponse(outputStream);

            if (!httpRequest.isQueryParamsEmpty()) {
                User user = getUser(httpRequest);
                passwordCheck(httpRequest, user);
            }

            httpResponse.transfer(httpRequest.getUrl());

        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void passwordCheck(final HttpRequest httpRequest, final User user) {
        if (user.checkPassword(httpRequest.getQueryParam("password"))) {
            LOG.debug("password Collect! : {}", user.getAccount());
            return;
        }
        LOG.error("password InCollect!!");
        throw new IllegalArgumentException("password InCollect");
    }

    private User getUser(final HttpRequest httpRequest) {
        String requestUserAccount = httpRequest.getQueryParam("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getQueryParam("account"));
        if (user.isPresent()) {
            LOG.debug("user Account : {}", user.get().getAccount());
            return user.get();
        }
        LOG.debug("user Not Exist!! : {}", requestUserAccount);
        throw new IllegalArgumentException("유저가 존재하지 않습니다");
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
