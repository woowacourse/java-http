package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

public class RequestHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(inputStream);
            final String method = httpRequest.getMethod();
            final String uri = httpRequest.getUri();

            byte[] body = new byte[0];
            HttpStatus status = HttpStatus.OK;
            String location = null;

            if ("/".equals(uri)) {
                body = Files.readAllBytes(getResources("/index.html").toPath());
                String response = makeResponse(ContentType.HTML, status, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (uri.endsWith(".html")) {
                body = Files.readAllBytes(getResources(uri).toPath());
                String response = makeResponse(ContentType.HTML, status, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (uri.endsWith(".css")) {
                body = Files.readAllBytes(getResources(uri).toPath());
                String response = makeResponse(ContentType.CSS, status, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (uri.endsWith(".js")) {
                body = Files.readAllBytes(getResources(uri).toPath());
                String response = makeResponse(ContentType.JS, status, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (uri.startsWith("/assets/img")) {
                body = Files.readAllBytes(getResources(uri).toPath());
                String response = makeResponse(ContentType.IMAGE, status, location, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            String fileName = uri;
            Map<String, String> requestBody = httpRequest.getParams();

            if (uri.startsWith("/login")) {
                fileName = "/login.html";

                if (requestBody.size() > 0) {
                    String account = httpRequest.getParameter("account");
                    String password = httpRequest.getParameter("password");

                    User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
                    if (user.checkPassword(password)) {
                        fileName = "/index.html";
                        status = HttpStatus.FOUND;
                        location = "/index.html";
                    } else {
                        fileName = "/401.html";
                        status = HttpStatus.FOUND;
                        location = "/401.html";
                    }
                }
            }

            if (uri.startsWith("/register")) {
                fileName = "/register.html";

                if (requestBody.size() > 0) {
                    String account = httpRequest.getParameter("account");
                    String email = httpRequest.getParameter("email");
                    String password = httpRequest.getParameter("password");
                    InMemoryUserRepository.save(new User(2L, account, password, email));
                    fileName = "/login.html";
                    status = HttpStatus.FOUND;
                    location = "/login.html";
                }
            }

            body = Files.readAllBytes(getResources(fileName).toPath());
            final String response = makeResponse(ContentType.HTML, status, location, body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String makeResponse(ContentType contentType, HttpStatus status, String location, byte[] body) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.getStatus(),
                "Content-Type: " + contentType.getType(),
                "Content-Length: " + body.length + " ",
                "Location: " + location,
                "",
                new String(body));
    }

    private File getResources(String fileName) {
        final URL resource = getClass().getClassLoader().getResource("static" + fileName);
        if (resource != null) {
            return new File(resource.getPath());
        }
        return getResources("/404.html");
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
