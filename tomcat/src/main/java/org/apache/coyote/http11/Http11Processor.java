package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpMethod.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            HttpRequest httpRequest = HttpRequest.of(bufferedReader);

            HttpResponse httpResponse = execute(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse execute(HttpRequest httpRequest) throws IOException, URISyntaxException {
        if (sessionManager.contains(httpRequest.getCookie("JSESSIONID")) &&
            httpRequest.isSamePath("/login", "/register")) {
            return redirect("/index.html");
        }

        if (httpRequest.isSamePath("/login") && httpRequest.getHttpMethod() == POST) {
            return login(httpRequest);
        }

        if (httpRequest.isSamePath("/register") && httpRequest.getHttpMethod() == POST) {
            return register(httpRequest);
        }

        return transferResponse(httpRequest);
    }

    private HttpResponse login(HttpRequest httpRequest) {
        Map<String, String> requestBody = httpRequest.getRequestBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.get("account"));

        if (user.isPresent()) {
            User loginUser = user.get();
            String password = requestBody.getOrDefault("password", "");

            if (loginUser.checkPassword(password)) {
                UUID uuid = UUID.randomUUID();

                Session session = new Session(uuid.toString());
                session.setAttribute("user", user);
                sessionManager.add(session);

                return redirect("/index.html")
                    .addHeader("Set-Cookie", "JSESSIONID=" + uuid);
            }
        }
        return redirect("/401.html");
    }

    private HttpResponse register(HttpRequest httpRequest) {
        Map<String, String> requestBody = httpRequest.getRequestBody();

        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        return redirect("/index.html");
    }

    private HttpResponse redirect(String locationUri) {
        return HttpResponse.from("HTTP/1.1", "302 FOUND")
            .addHeader("Location", locationUri);
    }

    private HttpResponse transferResponse(HttpRequest httpRequest) throws IOException, URISyntaxException {
        String responseBody = readResponseBody(httpRequest);
        ContentType contentType = ContentType.findByPath(httpRequest.getPath());
        String contentLength = Integer.toString(responseBody.getBytes().length);

        return HttpResponse.from("HTTP/1.1", "200 OK")
            .addHeader("Content-Type", contentType.getType())
            .addHeader("Content-Length", contentLength)
            .addResponseBody(responseBody);
    }

    private String readResponseBody(HttpRequest httpRequest) throws IOException, URISyntaxException {
        if (httpRequest.isSamePath("/")) {
            return "Hello world!";
        }

        Path path = getPath(httpRequest);
        return new String(Files.readAllBytes(path));
    }

    private Path getPath(HttpRequest httpRequest) throws URISyntaxException {
        String path = httpRequest.getPath();

        if (httpRequest.isSamePath("/login", "/register")) {
            path += ".html";
        }

        return Paths.get(getClass()
            .getClassLoader()
            .getResource("static" + path)
            .toURI());
    }
}
