package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.file.DefaultFileHandler;
import org.apache.coyote.file.FileHandler;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final FileHandler fileHandler;

    public Http11Processor(final Socket connection) {
        this(connection, new DefaultFileHandler());
    }

    public Http11Processor(final Socket connection, final FileHandler fileHandler) {
        this.connection = connection;
        this.fileHandler = fileHandler;
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

            Request request = Request.from(bufferedReader.readLine());
            String responseBody = fileHandler.getFileLines(request.getRequestUrl());

            String extension = request.getRequestExtension();
            Response response = new Response("HTTP/1.1", HttpStatus.OK, ContentType.from(extension), responseBody);

            outputStream.write(response.createHttpResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
