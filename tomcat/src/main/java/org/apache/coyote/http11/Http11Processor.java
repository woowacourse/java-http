package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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

            if (request.isGetMethod()) {
                handleGetRequest(request, outputStream);
                return;
            }
            if (request.isPostMethod()) {
                handlePostRequest(request, outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleGetRequest(HttpRequest request, OutputStream outputStream) throws IOException {
        String path = request.getPath();
        if ("/favicon.ico".equals(path)) {
            handleFaviconRequest(outputStream);
            return;
        }
        if ("/login".equals(path) && doesLoggedIn(request.getCookie())) {
            redirectTo("/index.html", outputStream);
            return;
        }
        serveStaticFile(addHtmlExtension(path), outputStream);
    }

    private void handleFaviconRequest(OutputStream outputStream) throws IOException {
        final var response = "HTTP/1.1 204 No Content \r\n" +
                "Content-Length: 0 \r\n" +
                "\r\n";
        writeResponse(outputStream, response);
    }

    private boolean doesLoggedIn(HttpCookie httpCookie) {
        Session session = sessionManager.findSession(httpCookie.get("JSESSIONID"));
        return session != null && session.doesExistAttribute("user");
    }

    public void handlePostRequest(HttpRequest request, OutputStream outputStream) throws IOException {
        String path = request.getPath();
        if ("/login".equals(path)) {
            handleLogin(request, outputStream);
            return;
        }
        if ("/register".equals(path)) {
            handleRegister(request, outputStream);
        }
    }

    private void handleLogin(HttpRequest request, OutputStream outputStream) throws IOException {
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
                redirectToHomeSettingCookie(session.getId(), outputStream);
                return;
            }
        }
        redirectTo("/401.html", outputStream);
    }

    private void handleRegister(HttpRequest request, OutputStream outputStream) throws IOException {
        Map<String, String> pairs = request.getBodyQueryString();

        InMemoryUserRepository.save(new User(pairs.get("account"), pairs.get("password"), pairs.get("email")));
        redirectTo("/index.html", outputStream);
    }

    private void redirectToHomeSettingCookie(String jSessionId, OutputStream outputStream) throws IOException {
        final var response = "HTTP/1.1 302 Found \r\n" +
                "Set-Cookie: JSESSIONID=" + jSessionId + " \r\n" +
                "Location: http://localhost:8080/index.html \r\n" +
                "\r\n";
        writeResponse(outputStream, response);
    }

    private void redirectTo(String location, OutputStream outputStream) throws IOException {
        final var response = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080" + location + " \r\n" +
                "\r\n";
        writeResponse(outputStream, response);
    }

    private String addHtmlExtension(String path) {
        if (!"/".equals(path) && !path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private void serveStaticFile(String path, OutputStream outputStream) throws IOException {
        var responseBody = getStaticFileContent(path);
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + getFileExtension(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        writeResponse(outputStream, response);
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

    private void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
