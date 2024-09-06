package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = new SessionManager();

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

            HttpRequest request = HttpRequestParser.parse(inputStream);

            if (request.isGetMethod() && resourceExists(request)) {
                URL resource = getClass().getResource("/static" + request.getPath());
                Path path = Path.of(resource.toURI());
                serveFile(path, outputStream);
            }

            if (request.isGetMethod() && request.getPath().equals("/")) {
                URL resource = getClass().getResource("/static/index.html");
                Path path = Path.of(resource.toURI());
                serveFile(path, outputStream);
            }
            if (request.isGetMethod() && request.getPath().equals("/register")) {
                URL resource = getClass().getResource("/static/register.html");
                Path path = Path.of(resource.toURI());
                serveFile(path, outputStream);
            }
            if (request.isGetMethod() && request.getPath().startsWith("/login")) {
                boolean hasJsessionId = request.getHeaders().findInCookie("JSESSIONID");

                if (request.getPath().contains("?")) {
                    int index = request.getPath().indexOf("?");
                    String queryString = request.getPath().substring(index + 1);

                    index = queryString.indexOf("&");
                    String account = queryString.substring("account=".length(), index);
                    String password = queryString.substring(index + 1 + "password=".length());

                    InMemoryUserRepository.findByAccount(account)
                            .ifPresent(user -> {
                                if (user.checkPassword(password)) {
                                    log.info("user : {}", InMemoryUserRepository.findByAccount(account).get());
                                }
                            });
                }

                if (hasJsessionId) {
                    String jsessionId = request.getHeaders().findJsessionId();
                    Session session = sessionManager.findSession(jsessionId);
                    if (session != null) {
                        URL resource = getClass().getResource("/static/index.html");
                        Path path = Path.of(resource.toURI());
                        serveFile(path, outputStream, "index.html");
                        return;
                    }
                }
                URL resource = getClass().getResource("/static/login.html");
                Path path = Path.of(resource.toURI());
                serveFile(path, outputStream);
            }

            if (request.isPostMethod() && request.getPath().equals("/register")) {
                String body = request.getBody();

                String[] bodyParts = body.split("&");
                String account = bodyParts[0].substring("account=".length());
                String email = bodyParts[1].substring("email=".length());
                String password = bodyParts[2].substring("password=".length());

                InMemoryUserRepository.save(new User(account, email, password));

                URL fakeResource = getClass().getResource("/static/index.html");
                Path path = Path.of(fakeResource.toURI());
                serveFile(path, outputStream, "index.html");
            }
            if (request.isPostMethod() && request.getPath().equals("/login")) {
                String[] bodyParts = request.getBody().split("&");
                String account = bodyParts[0].substring("account=".length());
                String password = bodyParts[1].substring("password=".length());

                boolean isValidPassword = false;
                Optional<User> savedUser = InMemoryUserRepository.findByAccount(account);
                if (savedUser.isPresent()) {
                    isValidPassword = savedUser.get().checkPassword(password);
                }

                boolean jsessionid = request.getHeaders().findInCookie("JSESSIONID");
                String setCookie = null;
                if (!jsessionid) {
                    UUID idValue = UUID.randomUUID();
                    Session session = new Session(idValue.toString());
                    session.setAttribute("user", savedUser.get());
                    sessionManager.add(session);

                    Session session1 = sessionManager.findSession(session.getId());
                    System.out.println("session1 = " + session1);
                    setCookie = "Set-Cookie: JSESSIONID=" + idValue;
                }

                URL resource = getClass().getResource("/static/index.html");
                Path path = Path.of(resource.toURI());
                if (isValidPassword) {
                    serveFile(path, outputStream, "index.html", setCookie);
                } else {
                    serveFile(path, outputStream, "401.html", setCookie);
                }
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void serveFile(Path path, OutputStream outputStream) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            List<String> rawBody = bufferedReader.lines().toList();
            String body = rawBody.stream()
                    .collect(Collectors.joining("\n")) + "\n";

            String contentType = Files.probeContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
        }
    }

    private void serveFile(Path path, OutputStream outputStream, String locationPath) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            List<String> rawBody = bufferedReader.lines().toList();
            String body = rawBody.stream()
                    .collect(Collectors.joining("\n")) + "\n";

            String contentType = Files.probeContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 302 FOUND ",
                    "Location: http://localhost:8080/" + locationPath,
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
        }
    }

    private void serveFile(Path path, OutputStream outputStream, String locationPath, String setCookie)
            throws IOException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            List<String> rawBody = bufferedReader.lines().toList();
            String body = rawBody.stream()
                    .collect(Collectors.joining("\n")) + "\n";

            String contentType = Files.probeContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 302 FOUND ",
                    (setCookie != null ? setCookie : ""),
                    "Location: http://localhost:8080/" + locationPath,
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
        }
    }

    private boolean resourceExists(HttpRequest request) {
        try {
            URL resource = getClass().getResource("/static" + request.getPath());
            Path.of(resource.toURI());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
