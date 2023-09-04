package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handlermapping.HandlerMapping;
import org.apache.coyote.handlermapping.HandlerMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String startLine = bufferedReader.readLine();
            if (startLine == null) {
                return;
            }

            HttpRequest request = createRequest(startLine, bufferedReader);
            HandlerMatcher handlerMatcher = new HandlerMatcher(HttpMethod.valueOf(request.method()), request.uri());

            if (!HandlerMapping.canHandle(handlerMatcher)) {
                Handler exceptionHandler = HandlerMapping.getExceptionHandler(HttpStatus.NOT_FOUND);
                outputStream.write(exceptionHandler.handle(request).getResponse().getBytes());
                outputStream.flush();
                return;
            }
            Handler handler = HandlerMapping.getHandler(handlerMatcher);
            HttpResponse response = handler.handle(request);
            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createRequest(String startLine, BufferedReader bufferedReader) throws IOException {
        HttpRequest request = new HttpRequest(startLine);
        String header = "";
        boolean haveToReadBody = false;
        int contentLength = 0;
        while (!(header = bufferedReader.readLine()).isBlank()) {
            String[] splitHeader = header.split(": ");
            request.setHeader(splitHeader[0], splitHeader[1]);
            if (HttpHeader.CONTENT_LENGTH.value().equals(splitHeader[0])) {
                haveToReadBody = true;
                contentLength = Integer.valueOf(splitHeader[1]);
            }
        }
        if (haveToReadBody) {
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String body = new String(buffer);
            request.setBody(body);
        }
        return request;
    }
}
