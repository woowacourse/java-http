package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String NOT_FOUND_FILE_PATH = "static/404.html";
    private static final String ROOT_PATH = "/";
    private static final String STATIC_TARGET_PATH = "static";

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

            HttpRequest httpRequest = HttpRequest.from(inputStream);
            HttpResponse response = createResponse(httpRequest);
            response.writeTo(outputStream);

            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(HttpRequest httpRequest) throws IOException {
        if (isRoot(httpRequest)) {
            return HttpResponse.ok(
                    "", "text/html", "Hello world!".getBytes(StandardCharsets.UTF_8));
        }
        return createResourceResponse(httpRequest);
    }

    private HttpResponse createResourceResponse(HttpRequest httpRequest) throws IOException {
        if (isPostLoginRequest(httpRequest)) {
            return createLoginResponse(httpRequest);
        }
        if (isPostRegisterRequest(httpRequest)) {
            return createRegisterResponse(httpRequest);
        }
        String resourcePath = STATIC_TARGET_PATH + httpRequest.getPath();
        if (isGetLoginRequest(httpRequest) && isLoggedIn(httpRequest)) {
            return HttpResponse.sendRedirect("", "/index.html");
        }
        if (isGetLoginRequest(httpRequest) || isGetRegisterRequest(httpRequest)) {
            resourcePath += ".html";
        }
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            return createNotFoundResponse();
        }
        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        return HttpResponse.ok("", getContentType(resource.getPath()), body);
    }

    private boolean isLoggedIn(HttpRequest httpRequest) throws IOException {
        HttpSession session = httpRequest.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    private HttpResponse createLoginResponse(HttpRequest httpRequest) throws IOException {
        if (!httpRequest.hasBodyParameters("account", "password")) {
            return HttpResponse.sendRedirect("", "401.html");
        }
        Optional<User> userOpt = InMemoryUserRepository.findByAccount(httpRequest.getBodyParameter("account"));
        if (userOpt.isEmpty()) {
            return HttpResponse.sendRedirect("", "/401.html");
        }

        User user = userOpt.get();
        String password = httpRequest.getBodyParameter("password");
        if (user.checkPassword(password)) {
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("user", user);
            log.info("로그인 성공! 아이디: {}", user.getAccount());
            String jsessionid = decideJsessionidToSet(httpRequest, session.getId());
            return HttpResponse.sendRedirect(jsessionid, "/index.html");
        }
        return HttpResponse.sendRedirect("", "/401.html");
    }

    private HttpResponse createRegisterResponse(HttpRequest httpRequest) {
        if (!httpRequest.hasBodyParameters("account", "password", "email")) {
            return HttpResponse.sendRedirect("", "/401.html");
        }
        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");
        String email = httpRequest.getBodyParameter("email");
        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        return HttpResponse.sendRedirect("", "/index.html");
    }

    private HttpResponse createNotFoundResponse() throws IOException {
        URL resource = getClass().getClassLoader().getResource(NOT_FOUND_FILE_PATH);

        if (resource == null) {
            return HttpResponse.notFound(
                    "", "text/plain", "404 NOT FOUND".getBytes(StandardCharsets.UTF_8));
        }

        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        return HttpResponse.notFound("", getContentType(resource.getPath()), body);
    }

    private String getContentType(String resource) {
        if (resource.endsWith(".html")) {
            return "text/html";
        }
        if (resource.endsWith(".css")) {
            return "text/css";
        }
        return "";
    }

    private String decideJsessionidToSet(HttpRequest httpRequest, String otherJsessionid) {
        String jsessionid = httpRequest.getJsessionid();
        if (jsessionid.equals(otherJsessionid)) {
            return "";
        }
        return otherJsessionid;
    }

    private boolean isRoot(HttpRequest httpRequest) {
        return httpRequest.isMatched("GET", ROOT_PATH);
    }

    private boolean isGetLoginRequest(HttpRequest httpRequest) {
        return httpRequest.isMatched("GET", "/login");
    }

    private boolean isGetRegisterRequest(HttpRequest httpRequest) {
        return httpRequest.isMatched("GET", "/register");
    }

    private boolean isPostLoginRequest(HttpRequest httpRequest) {
        return httpRequest.isMatched("POST", "/login");
    }

    private boolean isPostRegisterRequest(HttpRequest httpRequest) {
        return httpRequest.isMatched("POST", "/register");
    }
}
