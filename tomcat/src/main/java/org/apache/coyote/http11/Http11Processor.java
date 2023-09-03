package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream();
            final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while (!("".equals(line = reader.readLine()))) {
                if (line == null) {
                    break;
                }
                stringBuilder.append(line).append(CRLF);
            }
            final String request = stringBuilder.toString();
            final HttpRequest httpRequest = HttpRequest.from(request);

            final HandlerMapping handlerMapping = HandlerMapping.find(
                httpRequest.getPath(),
                httpRequest.getHttpMethod());

            final Handler handler = new Handler(handlerMapping);
            final HttpResponse httpResponse = handler.makeResponse();

            outputStream.write(httpResponse.makeToString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
