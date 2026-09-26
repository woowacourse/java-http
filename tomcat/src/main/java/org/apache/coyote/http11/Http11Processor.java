package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
            final var response = buildResponseWith(inputStream);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String buildResponseWith(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = HttpRequest.from(reader);

        Session session = request.getSession();
        String response = routeRequest(request, session);
        if (request.isNewSession()) {
            return withSessionCookie(response, session.getId());
        }
        return response;
    }

    private String routeRequest(HttpRequest request, Session session) throws IOException {
        String path = request.getPath();

        if (path.startsWith("/login")) {
            if (request.isGet() && isLoggedIn(session)) {
                return redirect("/index.html");
            }
            if (request.isPost()) {
                return loginResponse(request, session);
            }
        }

        if (path.startsWith("/register") && request.isPost()) {
            return registerResponse(request);
        }

        return staticResponse(path);
    }

    private String loginResponse(HttpRequest request, Session session) {
        Optional<User> account = findAccount(request.getParameter("account"), request.getParameter("password"));
        if (account.isEmpty()) {
            return redirect("/401.html");
        }

        session.setAttribute("user", account.get());
        return redirect("/index.html");
    }

    private boolean isLoggedIn(Session session) {
        return session.getAttribute("user") != null;
    }

    private String registerResponse(HttpRequest request) {
        User user = new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        InMemoryUserRepository.save(user);
        return redirect("/index.html");
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

    private String redirect(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + location + " "
        );
    }

    private String withSessionCookie(String response, String jSessionId) {
        int statusLineEnd = response.indexOf("\r\n");
        return response.substring(0, statusLineEnd)
                + "\r\nSet-Cookie: JSESSIONID=" + jSessionId + " "
                + response.substring(statusLineEnd);
    }

    private String staticResponse(String path) throws IOException {
        String contentType = contentTypeOf(path);
        String responseBody = resolveContentOf(path);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
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
