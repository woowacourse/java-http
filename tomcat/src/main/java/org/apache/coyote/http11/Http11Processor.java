package org.apache.coyote.http11;

import static org.apache.coyote.http11.request.HttpMethod.*;
import static org.apache.coyote.http11.response.StatusCode.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";

    private static final String INDEX_HTML = "/index.html";
    private static final String LOGIN_HTML = "/login.html";
    private static final String REGISTER_HTML = "/register.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

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
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            HttpResponse httpResponse = execute(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse execute(HttpRequest httpRequest) throws IOException, URISyntaxException {
        if (sessionManager.contains(httpRequest.getCookie("JSESSIONID")) &&
            httpRequest.isSamePath(LOGIN_PATH, REGISTER_PATH)) {
            return redirect(INDEX_HTML);
        }

        if (httpRequest.isSamePath(LOGIN_PATH)) {
            if (httpRequest.getHttpMethod() == POST) {
                return login(httpRequest);
            }
            return transferResponse(LOGIN_HTML);
        }

        if (httpRequest.isSamePath(REGISTER_PATH)) {
            if (httpRequest.getHttpMethod() == POST) {
                return register(httpRequest);
            }
            return transferResponse(REGISTER_HTML);
        }

        return transferResponse(httpRequest.getPath());
    }

    private HttpResponse login(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.get("account"));

        if (user.isPresent()) {
            User loginUser = user.get();
            String password = requestBody.get("password");

            if (loginUser.checkPassword(password)) {
                UUID uuid = UUID.randomUUID();

                Session session = new Session(uuid.toString());
                session.setAttribute("user", user);
                sessionManager.add(session);

                return redirect(INDEX_HTML)
                    .addHeader("Set-Cookie", "JSESSIONID=" + uuid);
            }
        }
        return redirect(UNAUTHORIZED_HTML);
    }

    private HttpResponse register(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();

        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        return redirect(INDEX_HTML);
    }

    private HttpResponse redirect(String locationUri) {
        return HttpResponse.of(HTTP_VERSION_1_1, FOUND)
            .addHeader("Location", locationUri);
    }

    private HttpResponse transferResponse(String input) throws IOException, URISyntaxException {
        Path path = Paths.get(getClass()
            .getClassLoader()
            .getResource("static" + input)
            .toURI());

        String responseBody = new String(Files.readAllBytes(path));
        String contentType = Files.probeContentType(path);
        String contentLength = Integer.toString(responseBody.getBytes().length);

        return HttpResponse.of(HTTP_VERSION_1_1, OK, responseBody)
            .addHeader("Content-Type", contentType)
            .addHeader("Content-Length", contentLength);
    }
}