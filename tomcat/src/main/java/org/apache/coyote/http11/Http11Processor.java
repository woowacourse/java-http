package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest request = new HttpRequest(bufferedReader);

            if (request.isValidRegisterRequest()) {
                request = registerUser(request);
            }

            if (request.isValidLoginRequest()) {
                request = loginUser(request);
            }

            final var response = createResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest registerUser(HttpRequest request) {
        InMemoryUserRepository.save(new User(request.getQueryParam("account"),
            request.getQueryParam("password"), request.getQueryParam("email")));

        return HttpRequest.of(HttpMethod.GET, "/index", HttpStatusCode.HTTP_STATUS_REDIRECTED);
    }

    private HttpRequest loginUser(HttpRequest request) {
        Optional<User> user = InMemoryUserRepository.findByAccount(request.getQueryParam("account"));

        if (user.isPresent() && user.get().checkPassword(request.getQueryParam("password"))) {
            log.info("user : " + user);
            HttpRequest redirectRequest =  HttpRequest.of(HttpMethod.GET, "/index", HttpStatusCode.HTTP_STATUS_REDIRECTED);
            redirectRequest.addCookie("JSESSIONID", String.valueOf(UUID.randomUUID()));
            return redirectRequest;
        }
        return HttpRequest.of(HttpMethod.GET, "/401", HttpStatusCode.HTTP_STATUS_UNAUTHORIZED);
    }

    private String createResponse(HttpRequest request) throws IOException {

        final URL resource = getClass().getClassLoader().getResource(request.getStaticPath());
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        return String.join("\r\n",
            "HTTP/1.1 " + request.getHttpStatusCode(),
            "Content-Type: " + request.getContentType() + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "Set-Cookie: " + request.getCookie(),
            "",
            responseBody);
    }
}
