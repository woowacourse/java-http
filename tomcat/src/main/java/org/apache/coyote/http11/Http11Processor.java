package org.apache.coyote.http11;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.BodyResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.file.DefaultFileHandler;
import org.apache.coyote.file.FileHandler;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeader;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.web.RequestHandler;
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
        try (final BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final OutputStream bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream())) {

            Request request = Request.from(bufferedReader.readLine());
            Response response = branchRequest(request);

            bufferedOutputStream.write(response.createHttpResponse().getBytes());
            bufferedOutputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response branchRequest(final Request request) throws IOException {
        if (request.isFileRequest()) {
            String responseBody = fileHandler.getFileLines(request.getRequestUrl());
            String extension = request.getRequestExtension();
            HttpHeaders httpHeaders = HttpHeaderFactory.create(
                    new Pair(HttpHeader.CONTENT_TYPE, ContentType.from(extension).getValue())
            );
            return new BodyResponse(HttpStatus.OK, httpHeaders, responseBody);
        }
        return new RequestHandler().handle(request);
    }
}
