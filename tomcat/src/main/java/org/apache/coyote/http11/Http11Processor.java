package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.ResourceLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
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

            HttpRequest httpRequest = HttpRequest.from(inputStream);

            String version = httpRequest.getVersion();
            Map<String, String> httpRequestHeaders = httpRequest.getHeaders();
            String httpMethod = httpRequest.getHttpMethod();
            String page = httpRequest.getPath();
            HttpResponse httpResponse;

            String responseBody;
            if (page.equals("/")) {
                responseBody = "Hello world!";
                httpResponse = HttpResponse.of(version, 200, "text/html", responseBody);
            } else if (page.startsWith("/login") && httpMethod.equals("POST")) {
                String requestBody = httpRequest.getBody();
                String account = requestBody.split("&")[0].split("=")[1];
                String password = requestBody.split("&")[1].split("=")[1];

                User user = InMemoryUserRepository.findByAccount(account).get();

                if (user.checkPassword(password)) {
                    log.info("user : {}", user);
                    SessionManager sessionManager = new SessionManager();
                    UUID jSessionId = UUID.randomUUID();
                    Session session = new Session(jSessionId.toString());
                    session.setAttribute("user", user);
                    sessionManager.add(session);

                    responseBody = new String(ResourceLoader.loadResource("static/index.html"));
                    httpResponse = HttpResponse.of(version, 200, "text/html", responseBody);
                    httpResponse.addHeader("Location", "/index.html");
                    httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + jSessionId);
                } else {
                    responseBody = new String(ResourceLoader.loadResource("static/401.html"));
                    httpResponse = HttpResponse.of(version, 200, "text/html", responseBody);
                    httpResponse.addHeader("Location", "/401.html");
                }
            } else if (page.startsWith("/login") && httpMethod.equals("GET")) {
                SessionManager sessionManager = new SessionManager();

                if (httpRequestHeaders.containsKey("Cookie") &&
                        httpRequestHeaders.get("Cookie").startsWith("JSESSIONID=")) {
                    String jSessionId = httpRequestHeaders.get("Cookie").split("=")[1];
                    Session session = sessionManager.findSession(jSessionId);

                    if (session != null && session.getAttribute("user") != null) {
                        responseBody = new String(ResourceLoader.loadResource("static/index.html"));
                        httpResponse = HttpResponse.of(version, 200, "text/html", responseBody);
                    } else {
                        responseBody = new String(ResourceLoader.loadResource("static" + page + ".html"));
                        httpResponse = HttpResponse.of(version, 200, "text/html", responseBody);
                    }
                } else {
                    responseBody = new String(ResourceLoader.loadResource("static" + page + ".html"));
                    httpResponse = HttpResponse.of(version, 200, "text/html", responseBody);
                }
            } else if (page.equals("/register") && httpMethod.equals("POST")) {
                String requestBody = httpRequest.getBody();
                String account = requestBody.split("&")[0].split("=")[1];
                String email = requestBody.split("&")[1].split("=")[1];
                String password = requestBody.split("&")[2].split("=")[1];
                InMemoryUserRepository.save(new User(account, email, password));
                responseBody = new String(ResourceLoader.loadResource("static/index.html"));
                httpResponse = HttpResponse.of(version, 200, "text/html", responseBody);
                httpResponse.addHeader("Location", "/index.html");
            } else if (page.startsWith("/css/")) {
                responseBody = new String(ResourceLoader.loadResource("static" + page));
                httpResponse = HttpResponse.of(version, 200, "text/css", responseBody);
            } else if (page.contains(".js")) {
                responseBody = new String(ResourceLoader.loadResource("static" + page));
                httpResponse = HttpResponse.of(version, 200, "text/javascript", responseBody);
            } else if (page.endsWith(".html")) {
                responseBody = new String(ResourceLoader.loadResource("static" + page));
                httpResponse = HttpResponse.of(version, 200, "text/html", responseBody);
            } else {
                responseBody = new String(ResourceLoader.loadResource("static" + page + ".html"));
                httpResponse = HttpResponse.of(version, 200, "text/html", responseBody);
            }
            httpResponse.send(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
