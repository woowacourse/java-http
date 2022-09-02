package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.HttpStatusCode.OK;
import static org.apache.coyote.http11.util.FileLoader.loadFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.NotFoundUrlException;
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
        try {
            final String responseBody = getResponseBody(httpRequest);
            final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
            return new HttpResponse.Builder()
                    .statusCode(OK)
                    .headers(httpHeaders)
                    .body(responseBody)
                    .build();
        } catch (RuntimeException e) {
            return responseErrorPage(httpRequest);
        }
    }

    private String getResponseBody(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isFileRequest()) {
            return loadFile(httpRequest.getUrl());
        }
        if (httpRequest.getUrl().equals("/login")) {
            validateRegister(httpRequest);
            return loadFile("/login.html");
        }
        if (httpRequest.getUrl().equals("/")) {
            return "Hello world!";
        }
        throw new NotFoundUrlException();
    }

    private void validateRegister(final HttpRequest httpRequest) {
        final String account = httpRequest.getQueryString("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.info("already register account : {}", account);
        }
    }

    private HttpHeaders getHttpHeaders(final HttpRequest httpRequest, final String responseBody) {
        return new HttpHeaders()
                .addHeader(CONTENT_TYPE, getContentType(httpRequest))
                .addHeader(CONTENT_LENGTH, responseBody.getBytes().length);
    }

    private String getContentType(final HttpRequest httpRequest) {
        return "text/" + httpRequest.getFileExtension() + ";charset=utf-8";
    }

    private HttpResponse responseErrorPage(final HttpRequest httpRequest) throws IOException {
        final String responseBody = loadFile("/404.html");
        final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
        return new HttpResponse.Builder()
                .statusCode(NOT_FOUND)
                .headers(httpHeaders)
                .body(responseBody)
                .build();
    }
}
