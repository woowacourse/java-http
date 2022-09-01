package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginHandler;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = createHttpRequest(bufferedReader);
            final HttpResponse httpResponse = createResponse(httpRequest);

            outputStream.write(httpResponse.toResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | NoSuchElementException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        List<String> requestHeader = new ArrayList<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.equals("")) {
                break;
            }
            requestHeader.add(line);
        }
        return new HttpRequest(requestLine, requestHeader);
    }

    private HttpResponse createResponse(HttpRequest httpRequest) throws IOException {
        if (FileExtension.hasFileExtension(httpRequest.getUri())) {
            Path filePath = findFilePath(httpRequest.getUri());
            String content = new String(Files.readAllBytes(filePath));

            String contentType = FileExtension.findContentType(httpRequest.getUri());
            return new HttpResponse("200 OK", contentType, content);
        }
        if (httpRequest.getUri().equals("/")) {
            String contentType = FileExtension.HTML.getContentType();
            return new HttpResponse("200 OK", contentType, "Hello world!");
        }
        if (httpRequest.getUri().startsWith("/login")) {
            Map<String, String> queryValues = UriParser.parseUri(httpRequest.getUri());
            LoginHandler loginHandler = new LoginHandler();
            User user = loginHandler.login(queryValues);

            Path filePath = findFilePath("/login.html");
            String content = new String(Files.readAllBytes(filePath));
            String contentType = FileExtension.HTML.getContentType();
            return new HttpResponse("200 OK", contentType, content);
        }
        String contentType = FileExtension.HTML.getContentType();
        return new HttpResponse("200 OK", contentType, "");
    }

    private Path findFilePath(String fileName) {
        try {
            return Path.of(Objects.requireNonNull(
                    this.getClass().getClassLoader().getResource("static" + fileName)).getPath());
        } catch (NullPointerException e) {
            throw new NoSuchElementException(fileName + " 파일이 존재하지 않습니다.");
        }
    }
}
