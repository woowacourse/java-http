package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import java.util.function.Function;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Status;
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
    public synchronized void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final String requestMessage = getRequestMessage(bufferedReader);
            final Request request = new Request(requestMessage);
            final Response response = Response.builder(HttpVersion.HTTP11)
                    .setJSessionIdCookie(request.getCookie())
                    .build();
            final BiFunction<Request, Response, Response> handler = HandlerMapper.of(request);
            final String rawResponse = handler.apply(request, response)
                    .getResponse();

            outputStream.write(rawResponse.getBytes());
            outputStream.flush();
            bufferedReader.close();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestMessage(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder actual = new StringBuilder();
        String line;
        while (true) {
            line = bufferedReader.readLine();
            if (line.equals("")) {
//                actual.append("")
//                        .append("\r\n");
                break;
            }
            actual.append(line)
                    .append("\r\n");
        }
        System.out.println(actual);
        actual.append("").append("\r\n");
        while (bufferedReader.ready()) {
            char a = (char) bufferedReader.read();
            actual.append(a);
        }
        return actual.toString();
    }
}
