package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.MimeType;
import org.apache.coyote.Processor;
import org.apache.coyote.exception.HttpParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {
    private static final byte[] DEFAULT_BODY = "Hello world!".getBytes();

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final HttpResponseProcessor responseProcessor = new HttpResponseProcessor(outputStream);
            try {
                final HttpRequest httpRequest = new Http11RequestProcessor(inputStream).process();
                if (processEndpoints(httpRequest, responseProcessor)) {
                    return;
                }
                responseProcessor.sendStaticResource(httpRequest.getPath());
            } catch (HttpParseException | URISyntaxException e) {
                log.warn("잘못된 HTTP 요청입니다.");
                responseProcessor.sendError(HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean processEndpoints(HttpRequest httpRequest, HttpResponseProcessor responseProcessor) throws IOException, URISyntaxException {
        if ("/".equals(httpRequest.getPath())) {
            responseProcessor.sendStaticResource(HttpStatus.OK, MimeType.TEXT_HTML, DEFAULT_BODY);
            return true;
        }
        if ("/login".equals(httpRequest.getPath())) {
            processLogin(httpRequest, responseProcessor);
            return true;
        }
        return false;
    }

    private void processLogin(HttpRequest httpRequest, HttpResponseProcessor responseProcessor) throws IOException, URISyntaxException {
        Optional<String> account = httpRequest.getQueryParameter("account");
        Optional<String> password = httpRequest.getQueryParameter("password");
        if (account.isEmpty() || password.isEmpty()) {
            responseProcessor.sendStaticResource("/login.html");
            return;
        }

        if (login(account.get(), password.get())) {
            responseProcessor.sendRedirect("/index.html");
            return;
        }
        responseProcessor.sendRedirect("/401.html");
    }

    private boolean login(String account, String password) {
        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
        loginUser.ifPresent(user -> log.info("user : {}", user));
        return loginUser.isPresent();
    }
}
