package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpRequestParser;
import com.techcourse.http.MimeType;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

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
             final var outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = HttpRequestParser.parse(inputStream);
            final String response = generateResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(HttpRequest request) {
        try {
            String path = request.getPath();
            String method = request.getMethod();
            if ("/".equals(path) && method.equals("GET")) {
                return rootPage();
            }
            if (path.equals("/login") && method.equals("GET")) {
                return login(request);
            }
            return getStaticResource(request);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return "HTTP/1.1 404 Not Found ";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "HTTP/1.1 500 Internal Server Error ";
        }
    }

    private String rootPage() {
        return """
                HTTP/1.1 200 OK \r
                Content-Type: text/html;charset=utf-8 \r
                Content-Length: 12 \r
                \r
                Hello world!""";
    }

    private String login(HttpRequest request) throws IOException {
        String account = request.getParameter("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.checkPassword(request.getParameter("password"))) {
            throw new IllegalArgumentException("Password or email is not correct");
        }

        log.info("user : {}", user);

        URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String responseBody = new String(Files.readAllBytes(Path.of(resource.getPath())));
        return String.join(
                CRLF,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private String getStaticResource(HttpRequest request) throws IOException {
        String requestPath = request.getPath();
        URL resource = getClass().getClassLoader().getResource("static" + requestPath);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found");
        }

        final String responseBody = new String(Files.readAllBytes(Path.of(resource.getPath())));
        String endPath = requestPath.substring(requestPath.lastIndexOf("/") + 1);
        String mimeType = MimeType.getMimeType(endPath);

        return String.join(
                CRLF,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }
}
