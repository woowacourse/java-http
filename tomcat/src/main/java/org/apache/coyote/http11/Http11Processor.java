package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.Request;
import org.apache.coyote.http11.httpmessage.Response;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final var request = extractRequest(inputStream);

            if (!request.isGetMethod()) {
                throw new IllegalStateException("아직 지원하지 않는 http 요청입니다.");
            }
            final var response = doGet(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Request extractRequest(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final var requestMessage = new StringBuilder();

        while (true) {
            final var buffer = bufferedReader.readLine();
            requestMessage.append(buffer)
                    .append("\r\n");
            if (buffer == null || buffer.length() == 0) {
                break;
            }
        }
        return Request.of(requestMessage.toString());
    }

    private Response doGet(final Request request) throws IOException {
        final var uri = request.getUri();

        if (uri.contains(".")) {
            return getResponseWithFileName(uri);
        }
        if (uri.contains("?")) {
            return getResponseWithQueryString(uri);
        }

        return getResponse(uri);
    }

    private Response getResponse(final String uri) throws IOException {
        if (uri.equals("/login")) {
            return getResponseWithFileName("/login.html");
        }
        if (uri.equals("/")) {
            return Response.okWithResponseBody(ContentType.HTML, "Hello world!");
        }
        return new Response();
    }

    private Response getResponseWithFileName(final String fileName) throws IOException {
        final var fileExtension = fileName.split("\\.")[1];

        final var contentType = ContentType.from(fileExtension);

        final var resource = getClass().getClassLoader().getResource("static" + fileName);
        final var path = new File(resource.getPath()).toPath();

        final var responseBody = new String(Files.readAllBytes(path));

        return Response.okWithResponseBody(contentType, responseBody);
    }

    private Response getResponseWithQueryString(final String uri) {
        if (uri.contains("/login")) {

            final Map<String, String> queryStrings = extractQueryStrings(uri);
            final Optional<User> user = InMemoryUserRepository.findByAccount(queryStrings.get("account"));

            if (user.isPresent() && user.get().checkPassword(queryStrings.get("password"))) {
                log.info("존재하는 유저입니다. ::: " + user);
                return Response.redirect(ContentType.HTML, "http://localhost:8080/index.html");
            }
            log.info("존재하지 않는 유저입니다. ::: " + queryStrings.get("account"));
            return Response.redirect(ContentType.HTML, "http://localhost:8080/401.html");
        }
        return new Response();
    }

    private Map<String, String> extractQueryStrings(final String uri) {
        final Map<String, String> queryStrings = new HashMap<>();
        final int index = uri.indexOf("?");

        for (final String queryString : uri.substring(index + 1).split("&")) {
            final String name = queryString.split("=")[0];
            final String value = queryString.split("=")[1];
            queryStrings.put(name, value);
        }
        return queryStrings;
    }
}
