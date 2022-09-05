package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.httpmessage.common.ContentType;
import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.request.requestline.QueryStrings;
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
            final Request request = extractRequest(inputStream);

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
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final StringBuilder requestMessage = new StringBuilder();

        while (true) {
            final String buffer = bufferedReader.readLine();
            requestMessage.append(buffer)
                    .append("\r\n");
            if (buffer == null || buffer.length() == 0) {
                break;
            }
        }
        return Request.of(requestMessage.toString());
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

    private Response getResponse(final Request request) throws IOException {
        if (request.isMatchUri("/login")) {
            return getResponseWithFileName("/login.html");
        }
        if (request.isMatchUri("/")) {
            return Response.okWithResponseBody(ContentType.HTML, "Hello world!");
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
        if (request.isMatchUri("/login")) {
            final QueryStrings queryStrings = request.getUri().getQueryStrings();
            final String account = queryStrings.getValue("account");
            final String password = queryStrings.getValue("password");

            if (InMemoryUserRepository.exist(account, password)) {
                final User user = InMemoryUserRepository.findByAccount(account).get();
                log.info("존재하는 유저입니다. ::: " + user);
                return Response.redirect(ContentType.HTML, "http://localhost:8080/index.html");
            }
            log.info("존재하지 않는 유저입니다. ::: " + queryStrings.getValue("account"));
            return Response.redirect(ContentType.HTML, "http://localhost:8080/401.html");
        }
        return new Response();
    }
}
