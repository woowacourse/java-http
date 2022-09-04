package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.FileAccessException;
import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.exception.StreamException;
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
        final OutputStream outputStream = getOutputStream(connection);
        try (final var inputStream = connection.getInputStream();
             outputStream;
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final RequestEntity requestEntity = extractRequestInfo(bufferedReader);
            final Controller controller = controllerMapping.getController(requestEntity.getUri());
            flushResponse(outputStream, makeResponse(controller, requestEntity));
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private OutputStream getOutputStream(final Socket connection) {
        try {
            return connection.getOutputStream();
        } catch (IOException e) {
            throw new StreamException();
        }
    }

    private RequestEntity extractRequestInfo(final BufferedReader bufferedReader) {
        String line;
        while (!(line = readLine(bufferedReader)).isBlank()) {
            if (HttpMethod.isStartWithAny(line)) {
                final String uri = getUri(line);
                final String queryString = getQueryString(line);
                return new RequestEntity(uri, queryString);
            }
        }
        throw new CustomNotFoundException("요청을 찾을 수 없습니다.");
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

    private String makeResponse(final Controller controller, final RequestEntity requestEntity) {
        try {
            return controller.execute(requestEntity)
                    .build();
        } catch (CustomNotFoundException e) {
            return new ResponseEntity().httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } catch (Exception e) {
            return new ResponseEntity().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
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
