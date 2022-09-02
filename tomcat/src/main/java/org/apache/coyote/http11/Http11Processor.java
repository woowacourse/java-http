package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatusCode.OK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static";

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
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = new HttpRequest(bufferedReader);
            final HttpResponse httpResponse = handleResponse(httpRequest);
            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleResponse(final HttpRequest httpRequest) throws IOException {
        final String responseBody = getResponseBody(httpRequest);
        final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
        return new HttpResponse.Builder()
                .statusCode(OK)
                .headers(httpHeaders)
                .body(responseBody)
                .build();
    }

    private String getResponseBody(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isFileRequest()) {
            final Path path = Paths.get(
                    ClassLoader.getSystemResource(STATIC_DIRECTORY + httpRequest.getUrl()).getPath());
            return new String(Files.readAllBytes(path));
        }
        if (httpRequest.getUrl().equals("/login")) {
            validateRegister(httpRequest);
            final Path path = Paths.get(ClassLoader.getSystemResource(STATIC_DIRECTORY + "/login.html").getPath());
            return new String(Files.readAllBytes(path));
        }
        return "Hello world!";
    }

    private void validateRegister(final HttpRequest httpRequest) {
        final String account = httpRequest.getQueryString("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.info("already register account : {}", account);
        }
    }

    private HttpHeaders getHttpHeaders(final HttpRequest httpRequest, final String responseBody) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        return httpHeaders
                .addHeader(CONTENT_TYPE, getContentType(httpRequest))
                .addHeader(CONTENT_LENGTH, responseBody.getBytes().length);
    }

    private String getContentType(final HttpRequest httpRequest) {
        return "text/" + httpRequest.getFileExtension() + ";charset=utf-8";
    }
}
