package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

            final var bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final HttpRequest request = HttpRequest.parse(bufferedReader);
            final String method = request.requestLine().method();
            final String requestPath = request.requestLine().path();
            final Map<String, String> headers = request.headers();

            final SessionManager sessionManager = SessionManager.getInstance();
            final String sessionId = new HttpCookie(headers.get("cookie")).getValue("JSESSIONID");
            Session session = sessionId == null ? null : sessionManager.findSession(sessionId);
            String setCookie = null;
            if (session == null) {
                session = new Session(UUID.randomUUID().toString());
                sessionManager.add(session);
                setCookie = "JSESSIONID=" + session.getId() + "; Path=/; HttpOnly";
            }

            final String requestBody = request.body();

            if ("POST".equals(method) && "/register".equals(requestPath)) {
                final Map<String, String> parameters = queryParameters(requestBody);
                final String account = parameters.get("account");
                final String password = parameters.get("password");
                final String email = parameters.get("email");
                if (account == null || password == null || email == null) {
                    final String response = "HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\n"
                            + cookieHeader(setCookie) + "\r\n";
                    outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                    return;
                }
                InMemoryUserRepository.save(new User(account, password, email));
                sendRedirect(outputStream, "/index.html", setCookie);
                return;
            }

            if ("/login".equals(requestPath) || ("GET".equals(method)
                    && !"/".equals(requestPath) && !"/register".equals(requestPath))) {
                RequestMapping mapping = new RequestMapping(
                        Map.of("/login", new LoginController(session)), new StaticResourceController());
                Controller controller = mapping.getController(request);
                controller.service(request, new HttpResponse(outputStream, setCookie));
                return;
            }

            final String responseBody = responseBody(requestPath);
            final String contentType = contentType(requestPath);

            final String response = "HTTP/1.1 200 OK \r\n"
                    + "Content-Type: " + contentType + " \r\n"
                    + "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " \r\n"
                    + cookieHeader(setCookie) + "\r\n" + responseBody;

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private void sendRedirect(final OutputStream outputStream, final String location,
                              final String setCookie) throws IOException {
        final String response = "HTTP/1.1 302 Found\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n"
                + cookieHeader(setCookie) + "\r\n";
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String cookieHeader(final String setCookie) {
        return setCookie == null ? "" : "Set-Cookie: " + setCookie + "\r\n";
    }

    private Map<String, String> queryParameters(final String queryString) {
        final Map<String, String> queryParameters = new HashMap<>();
        for (String parameter : queryString.split("&")) {
            final String[] nameAndValue = parameter.split("=", 2);
            if (nameAndValue.length == 2) {
                queryParameters.put(URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8));
            }
        }
        return queryParameters;
    }

    private String responseBody(final String requestPath) throws IOException {
        if ("/".equals(requestPath)) {
            return "Hello world!";
        }

        final String resourcePath;
        if ("/login".equals(requestPath) || "/register".equals(requestPath)) {
            resourcePath = requestPath + ".html";
        } else {
            resourcePath = requestPath;
        }
        final URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            return "";
        }

        return Files.readString(Path.of(resource.getPath()), StandardCharsets.UTF_8);
    }

    private String contentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
