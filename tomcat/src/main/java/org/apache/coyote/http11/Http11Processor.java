package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.exception.FileAccessException;
import nextstep.jwp.support.ErrorView;
import nextstep.jwp.support.Resource;
import org.apache.coyote.Processor;
import org.apache.http.HttpMethod;
import org.apache.http.HttpStatus;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

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
            final Controller controller = controllerMapping.getController(requestEntity.getUri());
            flushResponse(outputStream, makeResponse(controller, requestEntity));
        } catch (CustomNotFoundException e) {
            flushResponse(outputStream, makeErrorResponse(HttpStatus.BAD_REQUEST, ErrorView.NOT_FOUND));
        } catch (Exception e) {
            flushResponse(outputStream, makeErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorView.INTERNAL_SERVER_ERROR));
        }
    }

    private RequestEntity extractRequestInfo(final BufferedReader bufferedReader) {
        String line;
        while (!(line = readLine(bufferedReader)).isBlank()) {
            if (HttpMethod.isStartWith(line)) {
                final String uri = getUri(line);
                final String queryString = getQueryString(line);
                return new RequestEntity(uri, queryString);
            }
        }
        throw new CustomNotFoundException("요청을 찾을 수 없습니다.");
    }

    private String makeResponse(final Controller controller, final RequestEntity requestEntity) {
        return controller.execute(requestEntity)
                .build();
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

    private String makeErrorResponse(final HttpStatus httpStatus, final ErrorView errorView) {
        final Resource resource = new Resource(errorView.getValue());
        return new ResponseEntity().httpStatus(httpStatus)
                .contentType(resource.getContentType())
                .content(resource.read())
                .build();
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
