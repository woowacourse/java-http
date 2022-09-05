package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import nextstep.jwp.UserService;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.httpmessage.common.ContentType;
import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.request.requestbody.RequestBodyContent;
import org.apache.coyote.http11.httpmessage.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final var request = Request.of(inputStream);

            if (request.isGetMethod()) {
                final var response = doGet(request);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            if (request.isPostMethod()) {
                final var response = doPost(request);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            throw new IllegalStateException("아직 지원하지 않는 http 요청입니다.");
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response doGet(final Request request) throws IOException {
        if (request.isFileRequest()) {
            return getResponseWithFileName(request.getUri().getResourcePath());
        }
        if (request.hasQueryString()) {
            return getResponseWithQueryString(request);
        }
        return getResponse(request);
    }

    private Response doPost(final Request request) {
        if (request.isMatchUri("/login")) {
            final RequestBodyContent userInput = RequestBodyContent.parse(request.getBody());
            final Optional<User> user = InMemoryUserRepository.findByAccount(userInput.get("account"));

            if (user.isPresent() && user.get().checkPassword(userInput.get("password"))) {
                log.info("존재하는 유저입니다. ::: " + user);

                if (request.hasHeader("Cookie")) {
                    return Response.redirect(ContentType.HTML, "/index.html");
                }
                final Session session = request.getSession(!request.hasHeader("Cookie"));
                log.info("새로운 sessionId ::: " + session.getId());
                session.setAttribute("user", user);
                return Response.redirect(ContentType.HTML, "/index.html")
                        .addHeader("Set-Cookie", "JSESSIONID="+ session.getId());
            }
            log.info("존재하지 않는 유저입니다. ::: " + userInput.get("account"));
            return Response.redirect(ContentType.HTML, "/401.html");
        }

        if (request.isMatchUri("/register")) {
            final RequestBodyContent userInput = RequestBodyContent.parse(request.getBody());
            UserService.save(userInput);

            return Response.redirect(ContentType.HTML, "http://localhost:8080/index.html");
        }
        return new Response();
    }

    private Response getResponse(final Request request) throws IOException {
        if (request.isMatchUri("/login")) {
            return getResponseWithFileName("/login.html");
        }
        if (request.isMatchUri("/")) {
            return Response.okWithResponseBody(ContentType.HTML, "Hello world!");
        }
        if (request.isMatchUri("/register")) {
            return getResponseWithFileName("/register.html");
        }
        return new Response();
    }

    private Response getResponseWithFileName(final String fileName) throws IOException {
        final String fileExtension = fileName.split("\\.")[1];

        final ContentType contentType = ContentType.from(fileExtension);
        final URL resource = getClass().getClassLoader().getResource("static" + fileName);
        final Path path = new File(resource.getPath()).toPath();

        final String responseBody = new String(Files.readAllBytes(path));

        return Response.okWithResponseBody(contentType, responseBody);
    }

    private Response getResponseWithQueryString(final Request request) {
        return new Response();
    }
}
