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

    public RequestHandler(final Socket connection) {
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

            if (httpRequest.getUrl().contains("login") && !httpRequest.isQueryParamsEmpty()) {
                userLogin(httpRequest, httpResponse);
                return;
            }
            httpResponse.transfer(httpRequest.getUrl());

        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void userLogin(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (isValidateUser(httpRequest)) {
            httpResponse.redirect301Transfer("/index.html");
        } else {
            httpResponse.redirect401Transfer("/401.html");
        }
    }

    private boolean isValidateUser(final HttpRequest httpRequest) {
        User user = getUser(httpRequest);
        return user != null && isCollectPassword(httpRequest, user);
    }

    private boolean isCollectPassword(final HttpRequest httpRequest, final User user) {
        if (user.checkPassword(httpRequest.getQueryParam("password"))) {
            LOG.debug("password Collect! : {}", user.getAccount());
            return true;
        }
        LOG.debug("password InCollect!! : {}", user.getAccount());
        return false;
    }

    private User getUser(final HttpRequest httpRequest) {
        String requestUserAccount = httpRequest.getQueryParam("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getQueryParam("account"));
        if (user.isPresent()) {
            LOG.debug("user Account : {}", user.get().getAccount());
            return user.get();
        }
        LOG.debug("user Not Exist!! : {}", requestUserAccount);
        return null;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
