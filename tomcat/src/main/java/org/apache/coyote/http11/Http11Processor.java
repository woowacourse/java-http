package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.exception.FileAccessException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.RequestEntity;
import nextstep.jwp.http.RequestInfo;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.support.*;
import org.apache.coyote.Processor;
import org.apache.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ControllerMapping controllerMapping = new ControllerMapping();

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
            process(outputStream, bufferedReader);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private void process(final OutputStream outputStream, final BufferedReader bufferedReader) {
        try {
            final RequestEntity requestEntity = extractRequestInfo(bufferedReader);
            final Controller controller = controllerMapping.getController(RequestInfo.of(requestEntity));
            flushResponse(outputStream, makeResponse(controller, requestEntity));
        } catch (UnauthorizedException e) {
            flushResponse(outputStream, makeErrorResponse(HttpStatus.UNAUTHORIZED, View.UNAUTHORIZED));
        } catch (CustomNotFoundException e) {
            flushResponse(outputStream, makeErrorResponse(HttpStatus.BAD_REQUEST, View.NOT_FOUND));
        } catch (Exception e) {
            flushResponse(outputStream, makeErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, View.INTERNAL_SERVER_ERROR));
        }
    }

    private RequestEntity extractRequestInfo(final BufferedReader bufferedReader) {
        String line;
        while (!(line = readLine(bufferedReader)).isBlank()) {
            final Optional<HttpMethod> httpMethod = HttpMethod.find(line);
            if (httpMethod.isPresent()) {
                final String uri = getUri(line);
                final String queryString = getQueryString(line);
                return new RequestEntity(httpMethod.get(), uri, queryString);
            }
        }
        throw new CustomNotFoundException("요청을 찾을 수 없습니다.");
    }

    private String makeResponse(final Controller controller, final RequestEntity requestEntity) {
        return controller.execute(requestEntity)
                .parse();
    }

    private String readLine(final BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new FileAccessException();
        }
    }

    private String getUri(final String line) {
        final String url = line.split(" ")[1];
        return url.split("\\?")[0];
    }

    private String getQueryString(final String line) {
        final String url = line.split(" ")[1];
        final String[] splited = url.split("\\?");
        if (splited.length <= 1) {
            return null;
        }
        return splited[1];
    }

    private String makeErrorResponse(final HttpStatus httpStatus, final View errorView) {
        final Resource resource = new Resource(errorView.getValue());
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue());
        return new ResponseEntity(headers).httpStatus(httpStatus)
                .content(resource.read())
                .parse();
    }

    private void flushResponse(final OutputStream outputStream, final String responseBody) {
        if (outputStream == null) {
            return;
        }
        try {
            outputStream.write(responseBody.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
