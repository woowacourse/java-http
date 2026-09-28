package org.apache.coyote.http11.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Http11ErrorResponder {
    private static final Logger log = LoggerFactory.getLogger(Http11ErrorResponder.class);

    private static final String SERVICE_UNAVAILABLE_MESSAGE = "서버가 혼잡하여 요청을 처리할 수 없습니다.";

    private Http11ErrorResponder() {
    }

    public static void sendServiceUnavailable(final Socket connection) {
        sendError(connection, HttpStatus.SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE_MESSAGE);
    }

    private static void sendError(final Socket connection, final HttpStatus status, final String message) {
        try (connection) {
            final HttpResponse response = new HttpResponse();
            response.sendError(status, message);

            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
