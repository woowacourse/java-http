package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.controller.Controller;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
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
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final String requestMessage = getRequestMessage(bufferedReader);
            final Request request = new Request(requestMessage);

            final Response response = Response.builder(HttpVersion.HTTP11, outputStream)
                    .setJSessionIdCookie(request.getCookie())
                    .build();
            final Controller controller = HandlerMapper.of(request);
            controller.service(request, response);
            bufferedReader.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestMessage(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder rawRequest = new StringBuilder();
        String line;
        while (true) {
            line = bufferedReader.readLine();
            if (line.equals("")) {
                break;
            }
            rawRequest.append(line)
                    .append("\r\n");
        }

        rawRequest.append("\r\n");
        while (bufferedReader.ready()) {
            char a = (char) bufferedReader.read();
            rawRequest.append(a);
        }
        return rawRequest.toString();
    }
}
