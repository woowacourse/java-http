package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.requestmapping.Registry;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String request = bufferedReader.readLine();

            final HttpRequest httpRequest = new HttpRequest(request);
            final HttpResponse response = Registry.handle(httpRequest);

            outputStream.write(makeHttpMessage(response).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            throw new IllegalArgumentException("요청을 정상적으로 처리하지 못했습니다.");
        }
    }

    private String makeHttpMessage(HttpResponse response) {
        return String.join("\r\n",
                "HTTP/1.1 " + response.getHttpStatus() + " ",
                "Content-Type: " + response.getHeader().getContentType() + ";charset=utf-8 ",
                "Content-Length: " + response.getBody().getBytes().length + " ",
                "",
                response.getBody());
    }
}
