package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.cookie.HttpCookie;
import org.apache.coyote.Processor;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
        ) {
            HttpRequest httpRequest = HttpRequest.from(bufferedInputStream);
            String method = httpRequest.getMethod();
            String pathUri = httpRequest.getPathUri();
            String requestBody = httpRequest.getRequestBody();
            HttpStatus httpStatus = HttpStatus.OK;

            if (pathUri.equals("/register") && method.equals("POST")) {
                if (isLogin(httpRequest.getHeader("Cookie"))) {
                    writeAndFlush(
                            outputStream,
                            new HttpResponse()
                                    .status(HttpStatus.FORBIDDEN)
                                    .contentType("plain")
                                    .body("권한이 없습니다.")
                    );
                    return;
                }
                handleRegister(requestBody, outputStream);
                return;
            }

            if (pathUri.equals("/login") && method.equals("GET")) {
                if (isLogin(httpRequest.getHeader("Cookie"))) {
                    writeAndFlush(
                            outputStream,
                            new HttpResponse().redirectTo("/index.html")
                    );
                    return;
                }
            }

            if (pathUri.equals("/logout") && method.equals("POST")) {
                handleLogout(httpRequest.getHeader("Cookie"));
                writeAndFlush(outputStream,
                        new HttpResponse()
                                .redirectTo("/login")
                                .expiresCookie("JSESSIONID", "/")
                );
                return;
            }

            if (pathUri.equals("/login") && method.equals("POST")) {
                User user = login(requestBody);

                if (user != null) {
                    String jsessionId = storeSession(httpRequest.getHeader("Cookie"), user);

                    writeAndFlush(
                            outputStream,
                            new HttpResponse()
                                    .addCookie("JSESSIONID", jsessionId)
                                    .redirectTo("/index.html")
                    );
                    return;
                }

                pathUri = "/401.html";
                httpStatus = HttpStatus.UNAUTHORIZED;
            }

            pathUri = normalizePathUri(pathUri);
            Path path = getPath("static" + pathUri);

            if (path == null) {
                pathUri = "/404.html";
                httpStatus = HttpStatus.NOT_FOUND;
                path = getPath("static" + pathUri);
            }

            String responseBody = findResponseBody(pathUri, path);
            String contentType = extractType(path);
            writeAndFlush(
                    outputStream,
                    new HttpResponse()
                            .status(httpStatus)
                            .contentType(contentType)
                            .body(responseBody)
            );

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String storeSession(String cookie, User user) {
        String jsessionId = getJsessionId(cookie);
        SessionManager sessionManager = SessionManager.getInstance();

        Session oldSession = sessionManager.findSession(jsessionId);
        if (oldSession != null) {
            oldSession.invalidate();
            sessionManager.remove(oldSession.getJsessionId());
        }

        Session newSession = new Session();
        sessionManager.add(newSession);
        newSession.setAttribute("user", user);

        return newSession.getJsessionId();
    }

    private static String getJsessionId(String cookie) {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.parseCookie(cookie);
        return httpCookie.getCookieValue("JSESSIONID");
    }

    private static void handleRegister(String requestBody, OutputStream outputStream) throws IOException {
        Map<String, String> requestBodyParams = parseQueryParams(requestBody);

        String account = requestBodyParams.get("account");
        String password = requestBodyParams.get("password");
        String email = requestBodyParams.get("email");

        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            writeAndFlush(
                    outputStream,
                    new HttpResponse()
                            .status(HttpStatus.BAD_REQUEST)
                            .body("요청이 잘못되었습니다.")
                            .contentType("plain")
            );
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        writeAndFlush(
                outputStream,
                new HttpResponse().redirectTo("/index.html")
        );
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static User login(String requestBody) {
        Map<String, String> queryParams = parseQueryParams(requestBody);

        if (queryParams.get("account") == null || queryParams.get("password") == null) {
            return null;
        }

        return InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .filter(user -> user.checkPassword(queryParams.get("password")))
                .orElse(null);
    }

    private static String findResponseBody(String pathUri, Path path) throws IOException {
        if (pathUri.equals("/")) {
            return "Hello world!";
        }

        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private static void writeAndFlush(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }

    private static String extractType(Path path) {
        if (path.toString().endsWith(".css")) {
            return "css";
        }
        if (path.toString().endsWith(".js")) {
            return "javascript";
        }
        return "html";
    }

    private static Map<String, String> parseQueryParams(String queryParams) {
        Map<String, String> queries = new HashMap<>();

        if (queryParams == null || queryParams.isBlank()) {
            return queries;
        }

        for (String parameter : queryParams.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }
            queries.put(keyValue[0], keyValue[1]);
        }

        return queries;
    }

    private static Path getPath(String filePath) throws URISyntaxException {
        URL resource = Http11Processor.class.getClassLoader().getResource(filePath);

        if (resource == null) {
            return null;
        }
        return Path.of(resource.toURI());
    }

    private void handleLogout(String cookie) {
        String jsessionId = getJsessionId(cookie);
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(jsessionId);

        if (session != null) {
            session.invalidate();
            sessionManager.remove(jsessionId);
        }
    }

    private boolean isLogin(String cookie) {
        String jsessionId = getJsessionId(cookie);

        if (jsessionId == null || jsessionId.isBlank()) {
            return false;
        }

        Session session = SessionManager.getInstance().findSession(jsessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private String normalizePathUri(String pathUri) {
        if (pathUri.equals("/")) {
            return "/";
        }
        if (!pathUri.contains(".")) {
            return pathUri + ".html";
        }
        return pathUri;
    }
}
