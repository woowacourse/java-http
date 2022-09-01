package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

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

            String request = bufferedReader.readLine();
            validateEmptyHeader(request);

            final var response = createResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | NoSuchElementException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void validateEmptyHeader(String request) {
        if (request == null) {
            throw new IllegalArgumentException("Http request header가 비어있습니다.");
        }
    }

    private String createResponse(String request) throws IOException {
        String[] requestUrlInfo = request.split(" ");
        if (requestUrlInfo[1].equals("/")) {
            return createOkResponse("Hello world!");
        }
        return createOkResponseWithContent(requestUrlInfo);
    }

    private String createOkResponse(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createOkResponseWithContent(String[] requestUrlInfo) throws IOException {
        String fileName = requestUrlInfo[1];
        Path filePath = findFilePath(fileName);
        String content = new String(Files.readAllBytes(filePath));
        return createOkResponse(content);
    }

    private Path findFilePath(String fileName) {
        try {
            return Path.of(Objects.requireNonNull(
                    this.getClass().getClassLoader().getResource("static/" + fileName)).getPath());
        } catch (NullPointerException e) {
            throw new NoSuchElementException(fileName + " 파일이 존재하지 않습니다.");
        }
    }
}
