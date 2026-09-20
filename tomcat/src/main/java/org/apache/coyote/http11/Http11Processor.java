package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
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
    private static final String USER_ATTRIBUTE = "user";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager manager;

    public Http11Processor(final Socket connection, final Manager manager) {
        this.connection = connection;
        this.manager = manager;
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
        String requestPath = httpRequest.getPath();
        if ("/".equals(requestPath)) {
            responseProcessor.sendStaticResource(HttpStatus.OK, MimeType.TEXT_HTML, DEFAULT_BODY);
            return true;
        }
        if ("/login".equals(requestPath)) {
            processLogin(httpRequest, responseProcessor);
            return true;
        }
        if ("/register".equals(requestPath)) {
            if (httpRequest.isGet()) {
                responseProcessor.sendStaticResource("/register.html");
                return true;
            }
            if (httpRequest.isPost()) {
                processRegister(httpRequest, responseProcessor);
                return true;
            }
        }
        return false;
    }

    private void processRegister(HttpRequest httpRequest, HttpResponseProcessor responseProcessor) throws IOException {
        Optional<String> account = httpRequest.getParameter("account");
        Optional<String> password = httpRequest.getParameter("password");
        Optional<String> email = httpRequest.getParameter("email");
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            responseProcessor.sendError(HttpStatus.BAD_REQUEST);
            return;
        }

        User user = new User(account.get(), password.get(), email.get());
        InMemoryUserRepository.save(user);
        log.info("registered user : {}", user);
        responseProcessor.sendRedirect("/index.html");
    }

    private void processLogin(HttpRequest httpRequest, HttpResponseProcessor responseProcessor) throws IOException, URISyntaxException {
        Optional<String> account = httpRequest.getParameter("account");
        Optional<String> password = httpRequest.getParameter("password");
        if (account.isEmpty() || password.isEmpty()) {
            showLoginPage(httpRequest, responseProcessor);
            return;
        }

        Optional<User> loginUser = login(account.get(), password.get());
        if (loginUser.isEmpty()) {
            responseProcessor.sendRedirect("/401.html");
            return;
        }
        doNewLogin(responseProcessor, loginUser.get());
    }

    private void showLoginPage(HttpRequest httpRequest, HttpResponseProcessor responseProcessor) throws IOException, URISyntaxException {
        if (findSession(httpRequest).isPresent()) {
            responseProcessor.sendRedirect("/index.html");
            return;
        }
        responseProcessor.sendStaticResource("/login.html");
    }

    private Optional<HttpSession> findSession(HttpRequest httpRequest) throws IOException {
        Optional<String> sessionId = httpRequest.getCookie(SessionManager.SESSION_ID)
                .map(Cookie::getValue);
        if (sessionId.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(manager.findSession(sessionId.get()));
    }

    private void doNewLogin(HttpResponseProcessor responseProcessor, User user) throws IOException {
        Session session = Session.create();
        session.setAttribute(USER_ATTRIBUTE, user);
        manager.add(session);

        Cookies cookies = Cookies.of(new Cookie(SessionManager.SESSION_ID, session.getId()));
        responseProcessor.sendRedirect("/index.html", cookies);
    }

    private Optional<User> login(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
