package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.from(br);
            if (request == null) {
                return;
            }

            HttpRequestStartLine startLine = request.getStartLine();
            String method = startLine.getHttpMethod();
            String uri = request.getUri();

            HttpResponse response = new HttpResponse();

            // 1. GET 요청 Route
            if ("GET".equals(method)) {
                if (uri.equals("/") || uri.isEmpty()) {
                    if (isLoggedIn(request)) {
                        response.redirect("/index.html");
                    } else {
                        response.writeText("Hello world!", "text/html;charset=utf-8");
                        response.writeResponse(outputStream);
                        return;
                    }
                    response.writeResponse(outputStream);
                    return;
                }
                if (uri.equals("/login.html")) {
                    if (isLoggedIn(request)) {
                        response.redirect("/index.html");
                        response.writeResponse(outputStream);
                        return;
                    }
                }
                if (isStatic(uri)) {
                    handleStatic(uri, response);
                    response.writeResponse(outputStream);
                    return;
                }
            }

            // 2. POST login & register 요청 Route
            if (uri.equals("/login")) {
                handleLogin(request, response);
                response.writeResponse(outputStream);
                return;
            }
            if (uri.equals("/register")) {
                handleRegister(request, response);
                response.writeResponse(outputStream);
                return;
            }
            // 3. 나머지
            response.setStatus(404, "Not Found");
            response.writeText("No Route", "text/html;charset=utf-8");
            response.writeResponse(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isLoggedIn(final HttpRequest request) {
        Session session = request.getSession(false);
        User loginUser = getUser(session);
        boolean loggedIn = (loginUser != null);
        return loggedIn;
    }

    private static boolean isStatic(final String uri) {
        return uri.endsWith(".html") || uri.endsWith(".css") || uri.endsWith(".js");
    }


    private void handleLogin(final HttpRequest request, final HttpResponse response) {
        String method = request.getStartLine().getHttpMethod();
        if ("GET".equals(method)) {
            response.redirect("/login.html");
            return;
        }
        Map<String, List<String>> queryParameters = request.getParameters();
        String account = getFirst(queryParameters, "account");
        String password = getFirst(queryParameters, "password");

        if (account == null || password == null) {
            response.redirect("/login.html");
            return;
        }

        boolean success = InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);

        if (success) {
            log.info("Login OK - account {}", account);
            Session session = request.getSession(true);
            InMemoryUserRepository.findByAccount(account).ifPresent(u -> session.setAttribute("user", u));

            String jsessionId = session.getId();
            String setCookie = HttpCookie.buildSetCookieHeader(jsessionId);
            response.addSetCookie(setCookie);

            response.redirect("/index.html");
            return;
        }

        log.info("Login FAILED - invalid password {}", account);
        response.redirect("/401.html");
    }

    private void handleRegister(final HttpRequest request, final HttpResponse response) {
        String method = request.getStartLine().getHttpMethod();
        if ("GET".equals(method)) {
            response.redirect("/register.html");
            return;
        }

        Map<String, List<String>> queryParameters = request.getParameters();
        String account = getFirst(queryParameters, "account");
        String email = getFirst(queryParameters, "email");
        String password = getFirst(queryParameters, "password");

        if (account != null && email != null && password != null) {
            InMemoryUserRepository.save(new User(account, password, email));
            log.info("Register OK - account {}", account);

            Session session = request.getSession(true);
            session.setAttribute("user", new User(account, password, email));
            String setCookie = HttpCookie.buildSetCookieHeader(session.getId());
            response.addSetCookie(setCookie);

            response.redirect("/index.html");
            return;
        }

        response.redirect("/register.html");
    }

    private void handleStatic(final String uri, final HttpResponse response) throws IOException, URISyntaxException {
        String cp = "static/" + (uri.startsWith("/") ? uri.substring(1) : uri);
        URL url = getClass().getClassLoader().getResource(cp);
        if (url == null) {
            response.setStatus(404, "Not Found");
            response.writeText("Requested resource was not found on the server.", "text/plain;charset=utf-8");
            return;
        }
        Path absolutePath = Path.of(url.toURI());
        if (!Files.exists(absolutePath) || Files.isDirectory(absolutePath)) {
            response.setStatus(500, "Internal Server Error");
            response.writeText("File not found", "text/plain;charset=utf-8");
            return;
        }
        byte[] bytes = Files.readAllBytes(absolutePath);
        String contentType = guessContentType(absolutePath.toString());
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setBody(bytes);
    }

    private String getFirst(final Map<String, List<String>> map, final String key) {
        List<String> list = map.get(key);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.getFirst();
    }


    private String guessContentType(final String target) {
        if (target.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (target.endsWith(".htm")) {
            return "text/html;charset=utf-8";
        }
        if (target.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (target.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return null;
    }

    private User getUser(Session session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("user");
    }
}
