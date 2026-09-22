package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
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
        if (httpRequest.isRoot()) {
            return HttpResponse.ok(
                    httpRequest.createJsessionidIfAbsent(), "text/html", "Hello world!".getBytes(StandardCharsets.UTF_8));
        }
        return createResourceResponse(httpRequest);
    }

    private HttpResponse createResourceResponse(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isPostLoginRequest()) {
            return createLoginResponse(httpRequest);
        }
        if (httpRequest.isPostRegisterRequest()) {
            return createRegisterResponse(httpRequest);
        }
        String resourcePath = httpRequest.getResourcePath();
        if (httpRequest.isGetLoginRequest() && isLoggedIn(httpRequest)) {
            return HttpResponse.sendRedirect("", "/index.html");
        }
        if (httpRequest.isGetLoginRequest() || httpRequest.isGetRegisterRequest()) {
            resourcePath += ".html";
        }
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            return createNotFoundResponse(httpRequest);
        }
        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        return HttpResponse.ok(httpRequest.createJsessionidIfAbsent(), getContentType(resource.getPath()), body);
    }

    private boolean isLoggedIn(HttpRequest httpRequest) throws IOException {
        Session session = httpRequest.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    private HttpResponse createLoginResponse(HttpRequest httpRequest) throws IOException {
        if (!httpRequest.hasBodyParameters("account", "password")) {
            return HttpResponse.sendRedirect(httpRequest.createJsessionidIfAbsent(), "401.html");
        }
        Optional<User> userOpt = InMemoryUserRepository.findByAccount(httpRequest.getBodyParameter("account"));
        if (userOpt.isEmpty()) {
            return HttpResponse.sendRedirect(httpRequest.createJsessionidIfAbsent(), "/401.html");
        }

        User user = userOpt.get();
        String password = httpRequest.getBodyParameter("password");
        if (user.checkPassword(password)) {
            Session session = httpRequest.getSession(true);
            session.setAttribute("user", user);
            log.info("로그인 성공! 아이디: {}", user.getAccount());
            String jsessionid = decideJsessionidToSet(httpRequest, session.getId());
            return HttpResponse.sendRedirect(jsessionid, "/index.html");
        }
        return HttpResponse.sendRedirect(httpRequest.createJsessionidIfAbsent(), "/401.html");
    }

    private HttpResponse createRegisterResponse(HttpRequest httpRequest) {
        if (!httpRequest.hasBodyParameters("account", "password", "email")) {
            return HttpResponse.sendRedirect(httpRequest.createJsessionidIfAbsent(), "/401.html");
        }
        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");
        String email = httpRequest.getBodyParameter("email");
        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        return HttpResponse.sendRedirect(httpRequest.createJsessionidIfAbsent(), "/index.html");
    }

    private HttpResponse createNotFoundResponse(HttpRequest httpRequest) throws IOException {
        URL resource = getClass().getClassLoader().getResource(NOT_FOUND_FILE_PATH);

        if (resource == null) {
            return HttpResponse.notFound(
                    httpRequest.createJsessionidIfAbsent(), "text/plain", "404 NOT FOUND".getBytes(StandardCharsets.UTF_8));
        }

        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        return HttpResponse.notFound(httpRequest.createJsessionidIfAbsent(), getContentType(resource.getPath()), body);
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
}
