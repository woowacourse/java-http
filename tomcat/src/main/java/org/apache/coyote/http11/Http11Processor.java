package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Parameters;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    // private static final int MAX_REQUEST_SIZE = 104_857_600; // 10MB

    private static final SessionManager SESSION_MANAGER = new SessionManager();
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
            HttpRequest request = new HttpRequest(inputStream);

            // response
            ContentType contentType = ContentType.NONE;
            HttpStatus httpStatus = HttpStatus.OK;
            Headers headers = new Headers();
            String responseBody = "";
            Cookies responseCookies = new Cookies();

            try {
                if (request.getPath().get().equals("/login")) {
                    if (request.getMethod() == HttpMethod.GET) {
                        if (getLoggedUser(request.getCookies()) == null) {
                            request.setPath("/login.html");
                        } else {
                            request.setPath("/index.html");
                        }
                    }
                    if (request.getMethod() == HttpMethod.POST) {
                        login(request.getBody(), responseCookies);
                        httpStatus = HttpStatus.FOUND;
                        headers.put("Location", "/index.html");
                    }
                }

                if (request.getPath().get().equals("/register")) {
                    if (request.getMethod() == HttpMethod.GET) {
                        request.setPath("/register.html");
                    }
                    if (request.getMethod() == HttpMethod.POST) {
                        register(request.getBody());
                        httpStatus = HttpStatus.FOUND;
                        headers.put("Location", "/index.html");
                    }
                }

                contentType = ContentType.fromPath(request.getPath());
            } catch (UnauthorizedException e) {
                contentType = ContentType.HTML;
                httpStatus = HttpStatus.UNAUTHORIZED;
                headers.clear();
                request.setPath("/401.html");
            } catch (IllegalArgumentException e) {
                contentType = ContentType.HTML;
                httpStatus = HttpStatus.NOT_FOUND;
                headers.clear();
                request.setPath("/404.html");
            }

            if (contentType.isText() && !httpStatus.is3xx()) {
                responseBody = getStaticPage(request.getPath().get());
            }
            final var response = buildResponse(httpStatus, contentType, headers, responseCookies, responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private User getLoggedUser(Cookies requestCookies) {
        String sessionId = requestCookies.get("JSESSIONID");
        if (sessionId == null) {
            return null;
        }
        Session session = SESSION_MANAGER.findSession(sessionId);
        if (session == null) {
            return null;
        }
        return (User)session.getAttribute("user");
    }

    private String buildResponse(HttpStatus status, ContentType contentType, Headers headers, Cookies cookies,
        String responseBody) {
        int bodyLength = getBodyLength(responseBody);
        return "HTTP/1.1 " + status.getCode() + " " + status.getName() + "\r\n"
            + "Content-Type: " + contentType.getType() + ";charset=utf-8" + "\r\n"
            + "Content-Length: " + bodyLength + "\r\n"
            + addIfNotEmpty(headers.toString())
            + addIfNotEmpty(cookies.toString())
            + "\r\n" + responseBody;
    }

    private String addIfNotEmpty(String value) {
        if (value.isEmpty()) {
            return "";
        }
        return value + "\r\n";
    }

    private int getBodyLength(String responseBody) {
        if (responseBody == null) {
            return 0;
        }
        return responseBody.getBytes().length;
    }

    private String getStaticPage(String requestPath) throws IOException, URISyntaxException {
        String normalizedPath = Paths.get(requestPath).normalize().toString();
        if (normalizedPath.contains("..")) {
            throw new IllegalArgumentException("존재하지 않는 페이지입니다.");
        }
        if (normalizedPath.equals("/") || normalizedPath.equals("\\")) {
            return "Hello world!";
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static" + normalizedPath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("존재하지 않는 페이지입니다.");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void login(Parameters queryParams, Cookies responseCookies) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = findUser(account, password);
        Session session = new Session();
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
        responseCookies.put("JSESSIONID", session.getId());
        log.info(user.toString());
    }

    private User findUser(String account, String password) {
        if (account == null || password == null) {
            throw new UnauthorizedException("필수 정보가 누락되었습니다.");
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            throw new UnauthorizedException("존재하지 않는 사용자입니다.");
        }
        if (!user.get().checkPassword(password)) {
            throw new UnauthorizedException("비밀번호가 틀렸습니다.");
        }
        return user.get();
    }

    private void register(Parameters parameters) {
        String account = parameters.get("account");
        String email = parameters.get("email");
        String password = parameters.get("password");
        InMemoryUserRepository.findByAccount(account)
            .ifPresent(user -> {
                throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
            });
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
