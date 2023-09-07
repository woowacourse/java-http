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
import org.apache.coyote.http11.exception.UnsupportedContentTypeException;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpResponse;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.session.Session;
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

            outputStream.write(httpResponse.convertToMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(final HttpRequest httpRequest) throws IOException {
        try {
            return mapRequestToResponse(httpRequest);
        } catch(UnsupportedContentTypeException exception) {
            return HttpResponse.of(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private HttpResponse mapRequestToResponse(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isRequestOf(HttpMethod.GET, "/")) {
            return HttpResponse.ofText(HttpStatus.OK, "Hello world!", httpRequest);
        }
        if (httpRequest.isRequestOf(HttpMethod.GET, "/login")) {
            final Session session = httpRequest.getSession(false);
            if (session == null) {
                return HttpResponse.ofFile(HttpStatus.FOUND, getFilePath("/login.html"), httpRequest);
            }
            return HttpResponse.ofFile(HttpStatus.FOUND, getFilePath("/index.html"), httpRequest);
        }

        if (httpRequest.isRequestOf(HttpMethod.POST, "/login")) {
            Session session;
            try {
                session = login(httpRequest);
            } catch (IllegalArgumentException e) {
                return HttpResponse.ofFile(HttpStatus.UNAUTHORIZED, getFilePath("/401.html"), httpRequest);
            }
            final HttpResponse httpResponse = HttpResponse.of(HttpStatus.FOUND);
            httpResponse.setHeader("Location", "/index.html");
            httpResponse.setCookie(session);
            return httpResponse;
        }

        if (httpRequest.isRequestOf(HttpMethod.GET, "/register")) {
            return HttpResponse.ofFile(HttpStatus.OK, getFilePath("/register.html"), httpRequest);
        }

        if (httpRequest.isRequestOf(HttpMethod.POST, "/register")) {
            try {
                register(httpRequest);
            } catch (IllegalArgumentException e) {
                return HttpResponse.ofFile(HttpStatus.CONFLICT, getFilePath("/login.html"), httpRequest);
            }
            final HttpResponse httpResponse = HttpResponse.of(HttpStatus.FOUND);
            httpResponse.setHeader("Location", "/index.html");
            return httpResponse;
        }

        return HttpResponse.ofFile(HttpStatus.OK, getFilePath(httpRequest.getPath()), httpRequest);
    }

    private Session login(final HttpRequest httpRequest) throws IllegalArgumentException {
        final User user = InMemoryUserRepository.findByAccount(httpRequest.getBodyOf("account"))
            .filter(foundUser -> foundUser.checkPassword(httpRequest.getBodyOf("password")))
            .orElseThrow(() -> new IllegalArgumentException("잘못된 로그인 정보입니다."));

        log.info(user.toString());
        final Session session = httpRequest.getSession(true);
        session.setAttribute("user", user);
        return session;
    }

    private void register(final HttpRequest httpRequest) {
        final String account = httpRequest.getBodyOf("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        final User user = new User(
            account, httpRequest.getBodyOf("password"), httpRequest.getBodyOf("email")
        );
        InMemoryUserRepository.save(user);
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
