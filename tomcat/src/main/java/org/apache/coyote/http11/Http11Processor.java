package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.SPACE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.auth.HttpCookie;
import org.apache.coyote.http11.auth.Session;
import org.apache.coyote.http11.auth.SessionManager;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponseEntity;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var requestLine = bufferedReader.readLine();

            final var request = HttpRequest.of(bufferedReader, requestLine);

            final var responseEntity = HttpResponseEntity.from(request);
            final var response = responseEntity.getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

}
