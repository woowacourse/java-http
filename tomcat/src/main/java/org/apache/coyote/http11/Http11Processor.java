package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.TEXT_CSS;
import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.UNAUTHORIZED;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String INDEX_HTML = "/index.html";
    private static final String JSESSIONID = "JSESSIONID";
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = handleRequest(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    // [TODO] 예외 처리 하는 exception handler 만들기
    private HttpResponse handleRequest(HttpRequest httpRequest) throws IOException {
        ResourceLoader resourceLoader = new ResourceLoader();

        if (httpRequest.method() == POST) {
            if (httpRequest.uri().equals("/register")) {
                return register(httpRequest);
            }
            if (httpRequest.uri().equals("/login")) {
                Optional<String> jsessionid = httpRequest.getCookie(JSESSIONID);
                if (jsessionid.isPresent()) {
                    return loginWithSession(jsessionid.get());
                }
                return login(httpRequest);
            }
            throw new IllegalArgumentException("지원되지 않는 uri 입니다");
        }
        if (httpRequest.method() == GET) {
            if (httpRequest.uri().equals("/")) {
                return HttpResponse.ok("Hello world!");
            }
            if (httpRequest.uri().equals("/login")) {
                Optional<String> jsessionid = httpRequest.getCookie(JSESSIONID);
                if (jsessionid.isPresent()) {
                    return loginWithSession(jsessionid.get());
                }
                return HttpResponse.ok(resourceLoader.loadResourceAsString("static/login.html"));
            }
            if (httpRequest.uri().equals("/register")) {
                return HttpResponse.ok(resourceLoader.loadResourceAsString("static/register.html"));
            }

            HttpResponse httpResponse =
                    HttpResponse.ok(resourceLoader.loadResourceAsString("static" + httpRequest.uri()));
            Optional<String> acceptHeader = httpRequest.getHeader("Accept");
            if (acceptHeader.isPresent() && acceptHeader.get().contains("text/css")) {
                httpResponse.setContentType(TEXT_CSS);
            }
            return httpResponse;
        }
        throw new IllegalArgumentException("지원되지 않는 요청입니다");
    }

    private HttpResponse register(HttpRequest httpRequest) {
        FormData formData = FormData.from(httpRequest.getBody());
        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다. 다른 아이디로 가입해주세요");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        HttpResponse httpResponse = HttpResponse.from(FOUND);
        httpResponse.sendRedirect(INDEX_HTML);
        return httpResponse;
    }

    private HttpResponse loginWithSession(String jsessionid) {
        SessionManager sessionManager = new SessionManager();
        Session session = sessionManager.findSession(jsessionid)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 세션 아이디입니다"));
        session.getAttribute("user")
                .orElseThrow(() -> new IllegalArgumentException("세션에 회원정보가 존재하지 않습니다"));
        HttpResponse httpResponse = HttpResponse.from(FOUND);
        httpResponse.sendRedirect(INDEX_HTML);
        return httpResponse;
    }

    private HttpResponse login(HttpRequest httpRequest) throws IOException {
        FormData formData = FormData.from(httpRequest.getBody());
        String account = formData.get("account");
        String password = formData.get("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            String jsessionid = UUID.randomUUID().toString();
            log.info(user.get().toString());
            Session session = new Session(jsessionid);
            session.setAttribute("user", user.get());
            SessionManager sessionManager = new SessionManager();
            sessionManager.add(session);
            return getRedirectResponseWithCookie(jsessionid);
        }
        return getUnauthorizedResponse();
    }

    private HttpResponse getRedirectResponseWithCookie(String jsessionid) {
        HttpResponse httpResponse = HttpResponse.from(FOUND);
        httpResponse.sendRedirect(INDEX_HTML);
        httpResponse.addCookie(JSESSIONID, jsessionid);
        return httpResponse;
    }

    private HttpResponse getUnauthorizedResponse() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/401.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return HttpResponse.from(UNAUTHORIZED, TEXT_HTML, responseBody);
    }
}
