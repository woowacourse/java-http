package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.SessionSupport;
import org.apache.coyote.http11.util.StaticResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
        ) {
            final var httpRequest = HttpRequest.from(inputStream);
            final var httpResponse = new HttpResponse(outputStream);

            final var session = SessionSupport.findSessionOrCreate(
                    manager, httpRequest.getRequestCookies(), httpResponse);

            //=========== POST 요청 처리 ============
            if (httpRequest.getMethod() == HttpMethod.POST) {
                final var parameters = httpRequest.getParameters();
                String redirectUrl = "/index.html";

                if ("/login".equals(httpRequest.getPath())) {
                    final var account = parameters.get("account");
                    final var password = parameters.get("password");
                    final Optional<User> optionalUser = findUserByAccount(account);

                    if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
                        SessionSupport.rotateSessionAfterLogin(manager, session, optionalUser.get(), httpResponse);
                        log.info("로그인 성공 account: {}", account);
                    } else {
                        redirectUrl = "/401.html";
                        log.info("로그인 실패 account: {}", account);
                    }
                }

                if ("/register".equals(httpRequest.getPath())) {
                    final var newUser = new User(
                            parameters.get("account"),
                            parameters.get("password"),
                            parameters.get("email")
                    );
                    InMemoryUserRepository.save(newUser);
                    log.info("Registered new user: {}", newUser.getAccount());
                }

                httpResponse.sendRedirect(redirectUrl);
                return;
            }

            //=========== GET 요청 처리 ============
            var requestPath = httpRequest.getPath();

            if ("/login".equals(requestPath) && session.getAttribute("user") != null) {
                httpResponse.sendRedirect("/index.html");
                return;
            }

            if (requestPath.isBlank() || "/".equals(requestPath)) {
                requestPath = "index.html";
            }

            final var responseBody = StaticResourceResolver.read(requestPath);
            if (responseBody == null) {
                httpResponse.sendNotFound();
                return;
            }
            final var mimeType = ContentType.from(requestPath).getMimeType();
            httpResponse.sendOk(mimeType, responseBody);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try (final var outputStream = connection.getOutputStream()) {
                final var response = new HttpResponse(outputStream);
                response.sendServerError();
            } catch (IOException ioEx) {
                log.error("500 에러 전송 실패: {}", ioEx.getMessage(), ioEx);
            }
        }
    }

    public Optional<User> findUserByAccount(final String account) {
        if (account == null || account.isBlank()) {
            return Optional.empty();
        }
        return InMemoryUserRepository.findByAccount(account);
    }
}
