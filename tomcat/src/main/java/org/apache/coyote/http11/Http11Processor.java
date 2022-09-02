package org.apache.coyote.http11;

import javassist.NotFoundException;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HttpStatus;
import nextstep.jwp.controller.RequestEntity;
import nextstep.jwp.controller.ResponseEntity;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            final RequestEntity requestEntity = extractRequestInfo(bufferedReader);
            final Controller controller = controllerMapping.getController(requestEntity.getUri());
            flushResponse(outputStream, makeResponse(controller, requestEntity));
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private String makeResponse(final Controller controller, final RequestEntity requestEntity) {
        try {
            final ResponseEntity responseEntity = controller.execute(requestEntity);
            return makeResponse(responseEntity);
        } catch (NotFoundException e) {
            return makeResponse(new ResponseEntity(HttpStatus.BAD_REQUEST, requestEntity.getContentType(), null));
        } catch (Exception e) {
            return makeResponse(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, requestEntity.getContentType(), null));
        }
    }

    private RequestEntity extractRequestInfo(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> requestInfoMapping = new HashMap<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            if (isAccept(line)) {
                final String backPart = line.split(" ")[1];
                final String contentType = backPart.split(",")[0];
                requestInfoMapping.put("contentType", contentType);
            }
            if (isStartsWithRequestMethod(line)) {
                final String uri = getUri(line);
                final String queryString = getQueryString(line);
                requestInfoMapping.put("uri", uri);
                requestInfoMapping.put("queryString", queryString);
            }
        }
        return new RequestEntity(requestInfoMapping.get("contentType"), requestInfoMapping.get("uri"), requestInfoMapping.get("queryString"));
    }

    private boolean isAccept(final String line) {
        final String[] splited = line.split(":");
        return splited.length != 0 && splited[0].equals("Accept");
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

    private boolean isStartsWithRequestMethod(final String line) {
        for (String requestMethod : List.of("GET", "POST", "PUT", "PATCH", "DELETE")) {
            if (line.startsWith(requestMethod)) {
                return true;
            }
        }
        return false;
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

    private String makeResponse(final ResponseEntity responseEntity) {
        return String.join("\r\n",
                responseEntity.getHttpVersion() + " " + responseEntity.getHttpStatus().getCode() + " " + responseEntity.getHttpStatus().name() + " ",
                "Content-Type: " + responseEntity.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + responseEntity.getContentLength() + " ",
                "",
                responseEntity.getContent());
    }
}
