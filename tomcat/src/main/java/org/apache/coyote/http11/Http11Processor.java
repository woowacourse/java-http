package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final SessionManager sessionManager = SessionManager.getInstance();

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
        try (var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = new HttpResponse(outputStream);

            if (request.isGetMethod()) {
                handleGetRequest(request, response);
                return;
            }
            if (request.isPostMethod()) {
                handlePostRequest(request, response);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleGetRequest(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        if ("/favicon.ico".equals(path)) {
            handleFaviconRequest(response);
            return;
        }
        if ("/login".equals(path) && doesLoggedIn(request.getCookie())) {
            redirectTo(response, "/index.html");
            return;
        }
        serveStaticFile(request, response);
    }

    private void handleFaviconRequest(HttpResponse response) throws IOException {
        response.addStatusLine("HTTP/1.1 204 No Content");
        response.addHeader("Content-Length", "0");
        response.writeResponse();
    }

    private boolean doesLoggedIn(HttpCookie httpCookie) {
        Session session = sessionManager.findSession(httpCookie.get("JSESSIONID"));
        return session != null && session.doesExistAttribute("user");
    }

    public void handlePostRequest(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        if ("/login".equals(path)) {
            handleLogin(request, response);
            return;
        }
        if ("/register".equals(path)) {
            handleRegister(request, response);
        }
    }

    private void handleLogin(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> pairs = request.getBodyQueryString();

        String account = pairs.get("account");
        String password = pairs.get("password");
        if (account != null & password != null & InMemoryUserRepository.doesExistAccount(account)) {
            User user = InMemoryUserRepository.findByAccount(account).get();
            if (user.checkPassword(password)) {
                log.info("로그인 성공 ! 아이디 : {}", user.getAccount());
                Session session = new Session(UUID.randomUUID().toString());
                session.addAttribute("user", user);
                sessionManager.add(session);
                redirectToHomeSettingCookie(response, session.getId());
                return;
            }
        }
        redirectTo(response, "/401.html");
    }

    private void handleRegister(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> pairs = request.getBodyQueryString();

        InMemoryUserRepository.save(new User(pairs.get("account"), pairs.get("password"), pairs.get("email")));
        redirectTo(response, "/index.html");
    }

    private void redirectToHomeSettingCookie(HttpResponse response, String jSessionId) throws IOException {
        response.addStatusLine("HTTP/1.1 302 Found");
        response.addHeader("Set-Cookie", "JSESSIONID=" + jSessionId);
        response.addHeader("Location", "http://localhost:8080/index.html");
        response.writeResponse();
    }

    private void redirectTo(HttpResponse response, String location) throws IOException {
        response.addStatusLine("HTTP/1.1 302 Found");
        response.addHeader("Location", "http://localhost:8080" + location);
        response.writeResponse();
    }

    private String addHtmlExtension(String path) {
        if (!"/".equals(path) && !path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private void serveStaticFile(HttpRequest request, HttpResponse response) throws IOException {
        String path = addHtmlExtension(request.getPath());
        String body = getStaticFileContent(path);

        response.addStatusLine("HTTP/1.1 200 OK");
        response.addHeader("Content-Type", "text/" + getFileExtension(path) + ";charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        response.addBody(body);
        response.writeResponse();
    }

    private String getStaticFileContent(String path) throws IOException {
        if (Objects.equals(path, "/")) {
            return "Hello world!";
        }
        String staticPath = "static" + path;
        File file = new File(getClass().getClassLoader().getResource(staticPath).getPath());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getFileExtension(String path) {
        if (Objects.equals(path, "/")) {
            return "html";
        }
        String[] splitPath = path.split("\\.");
        return splitPath[splitPath.length - 1];
    }
}
