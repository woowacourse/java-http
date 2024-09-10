package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.Cookie;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

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

            HttpRequest httpRequest = HttpRequestParser.parse(inputStream);

            String path = httpRequest.getPath();

            if (path.equals("/")) {
                processHome(outputStream);
            }
            if (path.startsWith("/login")) {
                processLogin(httpRequest, outputStream);
            }
            if (path.equals("/register")) {
                processRegister(httpRequest, outputStream);
            }
            processFilesWithStatus(outputStream, httpRequest.getPath(), httpRequest.getFileType(), 200, "OK");
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processHome(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processFilesWithStatus(OutputStream outputStream, String path, String fileType, Integer httpStatusCode, String HttpStatusPhrase) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            return;
        }
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final var response = String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode + " " + HttpStatusPhrase,
                "Content-Type: text/" + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processFilesWithStatus(OutputStream outputStream, String path, String fileType, Integer httpStatusCode, String HttpStatusPhrase, String Cookie) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            return;
        }
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final var response = String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode + " " + HttpStatusPhrase,
                "Set-Cookie:" + Cookie+ " ",
                "Content-Type: text/" + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processRegister(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        if (httpRequest.getMethod().equals("GET")) {
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        }
        if (httpRequest.getMethod().equals("POST")) {
            Map<String, List<String>> queryParams = httpRequest.getQueryParams();
            String account = queryParams.get("account").get(0);
            String password = queryParams.get("password").get(0);
            String email = queryParams.get("email").get(0);
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            processFilesWithStatus(outputStream, "/index.html", "html", 200, "OK");
        }
    }

    private void processLogin(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        if (httpRequest.getMethod().equals("GET")) {
            Map<String, String> cookies = httpRequest.getCookies();
            if (cookies.containsKey("JSESSIONID")) {
                if (sessionManager.findSession(cookies.get("JSESSIONID")) != null) {
                    processFilesWithStatus(outputStream, "/index.html", "html", 302, "Found");
                }
            }
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        }

        if (httpRequest.getMethod().equals("POST")) {
            Map<String, List<String>> body = httpRequest.getBody();
            User user = InMemoryUserRepository.findByAccount(body.get("account").getFirst()).get();
            log.info("user : {}", user);
            if (!body.containsKey("password")) {
                return;
            }
            if (user.checkPassword(body.get("password").getFirst())) {
                Session session = Session.createRandomSession();
                session.setAttribute("user", user);
                Http11Cookie http11Cookie = Http11Cookie.sessionCookie(session.getId());
                sessionManager.add(session);
                processFilesWithStatus(outputStream, "/index.html", "html", 302, "Found", http11Cookie.toString());
                return;
            }
            processFilesWithStatus(outputStream, "/401.html", "html", 401, "Unauthorized");
        }

    }

}
