package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpRequestParser;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.MimeType;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()
        ) {
            HttpRequest request = HttpRequestParser.parse(inputStream);
            String response = generateResponse(request);

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
                return HttpResponse.ok("Hello world!")
                        .setContentType(MimeType.HTML.getMimeType())
                        .build();
            }
            if (path.equals("/login") && method.equals("GET")) {
                return login(request).build();
            }
            return getStaticResourceResponse(request.getPath()).build();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpResponse.internalServerError().build();
        }
    }

    private HttpResponse login(HttpRequest request) {
        String account = request.getParameter("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(request.getParameter("password"))) {
            return HttpResponse.found("/401.html");
        }

        log.info("user : {}", user.get());

        return HttpResponse.found("/index.html");
    }

    private HttpResponse getStaticResourceResponse(String requestPath) throws IOException {
        final String responseBody = readResource("static" + requestPath);
        String endPath = requestPath.substring(requestPath.lastIndexOf("/") + 1);
        String mimeType = MimeType.from(endPath);

        return HttpResponse.ok(responseBody)
                .setContentType(mimeType);
    }

    private String readResource(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found");
        }
        return new String(Files.readAllBytes(Path.of(resource.getPath())));
    }
}
