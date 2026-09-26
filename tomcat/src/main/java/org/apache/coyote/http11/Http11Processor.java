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
import java.nio.file.Files;
import java.util.Optional;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(reader);
            HttpResponse response = new HttpResponse();

            Session session = request.getSession();
            routeRequest(request, response, session);
            if (request.isNewSession()) {
                response.addCookie("JSESSIONID", session.getId());
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void routeRequest(HttpRequest request, HttpResponse response, Session session) throws IOException {
        String path = request.getPath();

        if (path.startsWith("/login")) {
            if (request.isGet() && isLoggedIn(session)) {
                response.sendRedirect("/index.html");
                return;
            }
            if (request.isPost()) {
                login(request, response, session);
                return;
            }
        }

        if (path.startsWith("/register") && request.isPost()) {
            register(request, response);
            return;
        }

        serveStaticResource(path, response);
    }

    private void login(HttpRequest request, HttpResponse response, Session session) {
        Optional<User> account = findAccount(request.getParameter("account"), request.getParameter("password"));
        if (account.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        session.setAttribute("user", account.get());
        response.sendRedirect("/index.html");
    }

    private boolean isLoggedIn(Session session) {
        return session.getAttribute("user") != null;
    }

    private void register(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }

    private Optional<User> findAccount(String account, String password) {
        if (account == null || password == null) {
            return Optional.empty();
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
            return user;
        }

        return Optional.empty();
    }

    private void serveStaticResource(String path, HttpResponse response) throws IOException {
        response.setBody(contentTypeOf(path), resolveContentOf(path));
    }

    private String resolveContentOf(String filePath) throws IOException {
        URL resource = getResource(filePath);
        if (!filePath.equals("/") && resource != null) {
            return Files.readString(new File(resource.getFile()).toPath());
        }
        return "Hello world!";
    }

    private URL getResource(String filePath) {
        String path = "static" + filePath;
        return getClass().getClassLoader().getResource(path);
    }

    private String contentTypeOf(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }
}
