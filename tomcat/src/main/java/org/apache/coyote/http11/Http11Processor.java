package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.SessionManager;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

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
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequestParser.from(inputStream)
                .toHttpRequest();

            HttpResponse httpResponse = createHttpResponseFrom(request);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createHttpResponseFrom(HttpRequest request) throws IOException {
        if (request.isLoginRequestWithAuthorization()) {
            return loginWithAuthorization(request);
        }

        if (request.isValidRegisterRequest()) {
            return registerUser(request);
        }

        if (request.isValidLoginRequest()) {
            return loginUser(request);
        }

        return createGetResponseFrom(request);
    }

    private HttpResponse loginWithAuthorization(HttpRequest request) throws IOException {
        Session session = request.getSession();

        if (SessionManager.getSessionManager().containsSession(session)) {
            Map<String, String> cookies = Map.of("JSESSIONID", session.getId());
            HttpCookie httpCookie = HttpCookie.of(cookies);

            return redirectTo("/index", HttpStatusCode.HTTP_STATUS_FOUND)
                .setCookie(httpCookie.toMessage());
        }
        return createGetResponseFrom(request);
    }

    private HttpResponse loginUser(HttpRequest request) throws IOException {
        Optional<User> user = InMemoryUserRepository.findByAccount(request.getBodyParam("account"));

        if (user.isEmpty()) {
            return redirectTo("/401", HttpStatusCode.HTTP_STATUS_UNAUTHORIZED);
        }

        User authorizedUser = user.get();
        if (authorizedUser.checkPassword(request.getBodyParam("password"))) {
            return redirectTo("/401", HttpStatusCode.HTTP_STATUS_UNAUTHORIZED);
        }

        Session session = request.getSession();
        session.setAttribute("user", authorizedUser);
        SessionManager.getSessionManager().add(session);

        Map<String, String> cookies = Map.of("JSESSIONID", session.getId());
        HttpCookie httpCookie = HttpCookie.of(cookies);

        return redirectTo("/index", HttpStatusCode.HTTP_STATUS_FOUND)
            .setCookie(httpCookie.toMessage());
    }

    private HttpResponse registerUser(HttpRequest request) throws IOException {
        InMemoryUserRepository.save(new User(request.getBodyParam("account"),
            request.getBodyParam("password"), request.getBodyParam("email")));

        return redirectTo("/index", HttpStatusCode.HTTP_STATUS_FOUND);
    }

    private HttpResponse redirectTo(String location, HttpStatusCode httpStatusCode) throws IOException {
        return createGetResponseFrom("static" + location + ".html", httpStatusCode, "text/html", location);
    }

    private HttpResponse createGetResponseFrom(HttpRequest request) throws IOException {
        return createGetResponseFrom(request.getResourcePath(), HttpStatusCode.HTTP_STATUS_OK, request.getContentType(),
            request.getPath());
    }

    private HttpResponse createGetResponseFrom(String resourcePath, HttpStatusCode httpStatusCode, String contentType,
        String location) throws IOException {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        return HttpResponse.of(httpStatusCode, responseBody)
            .setContentType(contentType + ";charset=utf-8")
            .setLocation(location);
    }
}
