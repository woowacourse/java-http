package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.util.ErrorResponder;
import org.apache.coyote.http11.util.HttpRequestIO;
import org.apache.coyote.http11.util.HttpResponseWriter;
import org.apache.coyote.http11.util.StaticResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager manager;

    public Http11Processor(final Socket connection, Manager manager) {
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
            final var headerReader = HttpRequestIO.createHeaderReader(inputStream);
            final var requestLine = RequestLine.from(headerReader.readLine());
            final var requestHeaders = RequestHeaders.from(headerReader);
            final var requestCookies = RequestCookies.from(requestHeaders.getHeader("Cookie"));
            final Map<String, String> responseHeaders = new HashMap<>();

            var session = manager.findSession(requestCookies.getCookie("JSESSIONID"));
            if (session == null) {
                session = Session.create(manager);
                final var sessionCookie = new ResponseCookie("JSESSIONID", session.getId());
                responseHeaders.put("Set-Cookie", sessionCookie.toHeaderString());
            }

            //=========== POST 요청 처리 ============
            if (requestLine.getMethod() == HttpMethod.POST) {
                final var requestBody = HttpRequestIO.readRequestBody(requestHeaders, inputStream);
                final Map<String, String> parameters = RequestBodyUtils.parseFormUrlEncoded(requestBody);
                String redirectUrl = "/index.html";

                if ("/login".equals(requestLine.getPath())) {
                    final var account = parameters.get("account");
                    final var password = parameters.get("password");
                    final Optional<User> optionalUser = findUserByAccount(account);
                    if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
                        final var oldSession = session;
                        session = Session.create(manager);
                        final var rotatedCookie = new ResponseCookie("JSESSIONID", session.getId());
                        responseHeaders.put("Set-Cookie", rotatedCookie.toHeaderString());
                        session.setAttribute("user", optionalUser.get());
                        oldSession.invalidate();

                        log.info("로그인 성공 account: {}", account);
                    } else {
                        redirectUrl = "/401.html";
                        log.info("로그인 실패 account: {}", account);
                    }
                }

                if ("/register".equals(requestLine.getPath())) {
                    final var newUser = new User(
                            parameters.get("account"),
                            parameters.get("password"),
                            parameters.get("email")
                    );
                    InMemoryUserRepository.save(newUser);
                    log.info("Registered new user: {}", newUser.getAccount());
                }

                final var response = HttpResponseWriter.redirect(redirectUrl, responseHeaders);
                HttpResponseWriter.write(outputStream, response);
                return;
            }

            //=========== GET 요청 처리 ============
            var requestPath = requestLine.getPath();

            if ("/login".equals(requestPath) && session.getAttribute("user") != null) {
                final var response = HttpResponseWriter.redirect("/index.html", responseHeaders);
                HttpResponseWriter.write(outputStream, response);
                return;
            }

            if (requestPath.isBlank() || "/".equals(requestPath)) {
                requestPath = "index.html";
            }

            final var responseBody = StaticResourceResolver.readAsString(requestPath);
            if (responseBody == null) {
                ErrorResponder.send404(outputStream);
                return;
            }
            final var mimeType = ContentType.from(requestPath).getMimeType();
            final var response = HttpResponseWriter.ok(mimeType, responseBody, responseHeaders);
            HttpResponseWriter.write(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try (final var outputStream = connection.getOutputStream()) {
                ErrorResponder.send500(outputStream);
            } catch (IOException ioEx) {
                log.error("500 에러 전송 실패: {}", ioEx.getMessage(), ioEx);
            }
        }
    }

    public Optional<User> findUserByAccount(String account) {
        if (account == null || account.isBlank()) {
            return Optional.empty();
        }
        return InMemoryUserRepository.findByAccount(account);
    }
}
