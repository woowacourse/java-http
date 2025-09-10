package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PATH = "static";

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            final HttpResponse response = getHttpResponse(httpRequest);

            outputStream.write(response.toString().getBytes(UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getHttpResponse(HttpRequest request) throws IOException {
        Session session = getSession(request.getHttpCookie());
        String requestMethod = request.getRequestMethod();
        String requestUriPath = request.getRequestUriPath();
        String body = request.getBody();
        if (requestMethod.equals("GET") && requestUriPath.equals("/")) {
            String responseBody = "Hello world!";
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(responseBody)
                .contentType("text/html;charset=utf-8")
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.endsWith(".css")) {
            String responseBody = readStaticFile(requestUriPath);
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(responseBody)
                .contentType("text/css;charset=utf-8")
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.endsWith(".html")) {
            String responseBody = readStaticFile(requestUriPath);
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(responseBody)
                .contentType("text/html;charset=utf-8")
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.endsWith(".js")) {
            String responseBody = readStaticFile(requestUriPath);
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .contentType("text/javascript;charset=utf-8")
                .body(responseBody)
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.equals("/register")) {
            String responseBody = readStaticFile("/register.html");
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .contentType("text/html;charset=utf-8")
                .body(responseBody)
                .build();
        }
        if (requestMethod.equals("POST") && requestUriPath.equals("/register")) {
            try {
                Map<String, String> formData = parseQueryParameters(body);
                register(formData);
            } catch (IllegalArgumentException e) {
                throw e;
            }
            return HttpResponse.builder()
                .status(HttpStatus.Found)
                .header("Location", "/index.html")
                .body("")
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.equals("/login")) {
            if (session != null) {
                Object user = session.getAttribute("user");
                if (user != null) {
                    return HttpResponse.builder()
                        .status(HttpStatus.Found)
                        .header("Location", "/index.html")
                        .body("")
                        .build();
                }
            }
            String responseBody = readStaticFile("/login.html");
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .contentType("text/html;charset=utf-8")
                .body(responseBody)
                .build();
        }
        if (requestMethod.equals("POST") && requestUriPath.equals("/login")) {
            try {
                Map<String, String> formData = parseQueryParameters(body);
                User loginUser = login(formData);
                if (session == null) {
                    session = new Session();
                    SessionManager.getInstance().add(session);
                } else {
                    SessionManager sessionManager = SessionManager.getInstance();
                    sessionManager.remove(session);
                    session.changeId();
                    sessionManager.add(session);
                }
                session.addAttribute("user", loginUser);
            } catch (UnAuthorizedException e) {
                return HttpResponse.builder()
                    .status(HttpStatus.Found)
                    .header("Location", "/401.html")
                    .body("")
                    .build();
            }
            return HttpResponse.builder()
                .status(HttpStatus.Found)
                .header("Location", "/index.html")
                .body("")
                .cookie("JSESSIONID", session.getId())
                .build();
        }
        throw new IllegalArgumentException("invalid request %s".formatted(requestUriPath));
    }

    private Session getSession(HttpCookie httpCookie) {
        if (httpCookie == null) {
            return null;
        }
        if (httpCookie.getCookie("JSESSIONID") == null) {
            return null;
        }
        String jsessionid = httpCookie.getCookie("JSESSIONID");
        SessionManager sessionManager = SessionManager.getInstance();
        return sessionManager.findSession(jsessionid);
    }

    private Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        Arrays.stream(queryString.split("&"))
            .map(parameter -> parameter.split("="))
            .forEach(keyValue -> queryParameters.put(keyValue[0], keyValue.length == 2 ? keyValue[1] : null));
        return Collections.unmodifiableMap(queryParameters);
    }

    private void register(Map<String, String> queryParameters) {
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        String email = queryParameters.get("email");
        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("account and password and email should be not null");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("user register account {} and email {}", user, email);
    }

    private User login(Map<String, String> queryParameters) {
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        if (account == null || password == null) {
            throw new UnAuthorizedException("account or password should be not null");
        }
        Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        User user = findUser.orElseThrow(() -> new UnAuthorizedException("Invalid account " + account));
        if (!user.checkPassword(password)) {
            throw new UnAuthorizedException("Invalid password");
        }
        log.atInfo().log("user: {}", user);
        return user;
    }

    private String readStaticFile(String filePath) throws IOException {
        String staticFilePath = STATIC_RESOURCE_PATH + filePath;
        URL resource = getClass().getClassLoader().getResource(staticFilePath);
        if (resource == null) {
            throw new IllegalArgumentException("리소스가 존재하지 않습니다. " + staticFilePath);
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
