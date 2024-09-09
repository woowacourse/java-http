package org.apache.coyote.http11;

import com.techcourse.auth.Session;
import com.techcourse.auth.SessionManager;
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
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String JSESSIONID = "JSESSIONID";

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
            String jSession = request.getCookie(JSESSIONID);
            if (isInvalidJSession(jSession)) {
                return HttpResponse.found("/login.html")
                        .setCookie(JSESSIONID, jSession).setCookie("Max-Age", "0")
                        .build();
            }

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
            if (path.equals("/register") && method.equals("GET")) {
                return getStaticResourceResponse("/register.html").build();
            }
            if (path.equals("/register") && method.equals("POST")) {
                return register(request).build();
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

    private boolean isInvalidJSession(String jSession) {
        return jSession != null && sessionManager.findSession(jSession) == null;
    }

    private HttpResponse login(HttpRequest request) throws IOException {
        String jSession = request.getCookie(JSESSIONID);
        if (request.getParameters().isEmpty() && jSession == null) {
            return getStaticResourceResponse("/login.html");
        }
        if (request.getParameters().isEmpty()) {
            return HttpResponse.found("/index.html");
        }

        String account = request.getParameter("account");
        Optional<User> userOpt = InMemoryUserRepository.findByAccount(account);

        if (userOpt.isEmpty() || !userOpt.get().checkPassword(request.getParameter("password"))) {
            return HttpResponse.found("/401.html");
        }

        User user = userOpt.get();
        log.info("user : {}", user);

        HttpResponse response = HttpResponse.found("/index.html");
        if (jSession == null) {
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);

            sessionManager.add(session);
            response.setCookie(JSESSIONID, session.getId());
        }
        return response;
    }

    private HttpResponse register(HttpRequest request) {
        InMemoryUserRepository.save(new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        ));

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
