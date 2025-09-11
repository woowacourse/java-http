package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    public static final String RESOURCE_DIRECTORY = "static";
    public static final String EXTENSION_SEPARATOR = ".";
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

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.from(bufferedReader);
            HttpResponse response = dispatchRequest(request);
            response.sendResponse(outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse dispatchRequest(HttpRequest request) throws IOException {
        if (request.isSameMethod("GET")) {
            return handleGetRequest(request);
        }
        if (request.isSameMethod("POST")) {
            return handlePostRequest(request);
        }
        return HttpResponse.of(HttpStatus.METHOD_NOT_ALLOWED, MimeType.HTML, "Method Not Allowed");
    }

    private HttpResponse handleGetRequest(HttpRequest request) throws IOException {
        if (request.isRootPath()) {
            return HttpResponse.of(HttpStatus.OK, MimeType.HTML, "Hello world!");
        }
        if (request.getPath().equals("/login") && request.hasParameters()) {
            return handleLogin(request);
        }
        if (request.getPath().equals("/login") && isLoggedIn(request)) {
            return HttpResponse.redirect("/index.html");
        }
        return handleStaticResource(request);
    }

    private HttpResponse handleStaticResource(HttpRequest request) throws IOException {
        MimeType mimeType = request.resolveMimeType();
        String path = ensureExtension(request, mimeType);
        Path filePath = getFilePath(path);
        if (filePath == null) {
            Path notFoundPath = getFilePath("/404.html");
            return buildResponseFromFile(notFoundPath, HttpStatus.NOT_FOUND, MimeType.HTML);
        }
        return buildResponseFromFile(filePath, HttpStatus.OK, mimeType);
    }

    private String ensureExtension(HttpRequest request, MimeType mimeType) {
        String path = request.getPath();
        if (request.getExtension().isEmpty()) {
            path += EXTENSION_SEPARATOR + mimeType;
        }
        return path;
    }

    private HttpResponse buildResponseFromFile(Path filePath, HttpStatus status, MimeType mimeType) throws IOException {
        String responseBody = Files.readString(filePath, StandardCharsets.UTF_8);
        return HttpResponse.of(status, mimeType, responseBody);
    }

    private HttpResponse handlePostRequest(HttpRequest request) throws IOException {
        if (request.getPath().equals("/login")) {
            return handleLogin(request);
        }
        if (request.getPath().equals("/register")) {
            return handleRegister(request);
        }
        return HttpResponse.of(HttpStatus.NOT_FOUND, request.resolveMimeType(), "Not Found");
    }

    private Path getFilePath(String path) {
        URL resource = getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + path);
        if (resource == null) {
            return null;
        }
        return new File(resource.getFile()).toPath();
    }

    private HttpResponse handleLogin(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            return HttpResponse.redirect("/401.html");
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> handleLoginSuccess(user, request))
                .orElseGet(() -> {
                    log.info("login failure: account= {}", account);
                    return HttpResponse.redirect("/401.html");
                });
    }

    private HttpResponse handleLoginSuccess(User user, HttpRequest request) {
        log.info("login success: account= {}", user.getAccount());
        HttpResponse httpResponse = HttpResponse.redirect("/index.html");
        String jsessionid = getOrCreateJsessionId(request, httpResponse);
        Session session = new Session(jsessionid);
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        return httpResponse;
    }

    private HttpResponse handleRegister(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        if (account == null || account.isBlank() || password == null || password.isBlank() || email == null
                || email.isBlank()) {
            return HttpResponse.redirect("/401.html");
        }

        return InMemoryUserRepository.findByAccount(account)
                .map(existingUser -> {
                    if (existingUser.checkPassword(password)) {
                        log.info("register failure: account= {} already registered", account);
                    } else {
                        log.info("register failure: duplicate account= {}", account);
                    }
                    return HttpResponse.redirect("/register.html");
                })
                .orElseGet(() -> {
                    User user = new User(account, password, email);
                    InMemoryUserRepository.save(user);
                    log.info("register success: account= {} email= {}", account, email);
                    return HttpResponse.redirect("/index.html");
                });
    }

    private String getOrCreateJsessionId(HttpRequest request, HttpResponse httpResponse) {
        String jsessionId = getJsessionId(request);
        if (jsessionId == null) {
            jsessionId = UUID.randomUUID().toString();
            httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + jsessionId);
        }
        return jsessionId;
    }

    private boolean isLoggedIn(HttpRequest request) {
        String jsessionId = getJsessionId(request);
        Session session = SessionManager.getInstance().findSession(jsessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private String getJsessionId(HttpRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        Cookie cookie = Cookie.fromHeader(cookieHeader);
        return cookie.get("JSESSIONID");
    }
}
