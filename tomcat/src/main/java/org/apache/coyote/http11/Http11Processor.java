package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpResponse;
import org.apache.coyote.http11.message.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final ClassLoader classLoader = getClass().getClassLoader();

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final HttpRequest httpRequest = HttpRequest.from(reader);

            final HttpResponse httpResponse = createResponse(httpRequest);
            final String message = httpResponse.convertToMessage();

            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isRequestOf(HttpMethod.GET, "/")) {
            return HttpResponse.ofText(HttpStatus.OK, "Hello world!", httpRequest);
        }
        if (httpRequest.isRequestOf(HttpMethod.GET, "/login")) {
            return HttpResponse.ofFile(HttpStatus.OK, getFilePath("/login.html"), httpRequest);
        }

        if (httpRequest.isRequestOf(HttpMethod.POST, "/login")) {
            try {
                login(httpRequest);
            } catch (IllegalArgumentException e) {
                return HttpResponse.ofFile(HttpStatus.UNAUTHORIZED, getFilePath("/401.html"), httpRequest);
            }
            final HttpResponse httpResponse = HttpResponse.of(HttpStatus.FOUND, httpRequest);
            httpResponse.setHeader("Location", "/index.html");
            return httpResponse;
        }

        return HttpResponse.ofFile(HttpStatus.OK, getFilePath(httpRequest.getPath()), httpRequest);
    }

    private void login(final HttpRequest httpRequest) throws IllegalArgumentException {
        final User user = InMemoryUserRepository.findByAccount(httpRequest.getBodyOf("account"))
            .filter(foundUser -> foundUser.checkPassword(httpRequest.getBodyOf("password")))
            .orElseThrow(() -> new IllegalArgumentException("잘못된 로그인 정보입니다."));

        log.info(user.toString());
    }

    private URL getFilePath(final String path){
        URL url = classLoader.getResource("static" + path);
        if (url == null) {
            url = classLoader.getResource("static/404.html");
        }
        return url;
    }
}
