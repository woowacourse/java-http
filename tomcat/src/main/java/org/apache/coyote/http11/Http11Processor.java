package org.apache.coyote.http11;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.web.FileRequestHandler;
import org.apache.coyote.web.RequestHandler;
import org.apache.coyote.web.request.Request;
import org.apache.coyote.web.request.RequestParser;
import org.apache.coyote.web.response.Response;
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
        try (final BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final OutputStream bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream())) {

            Request request = RequestParser.parse(bufferedReader);
            Response response = branchRequest(request);

            bufferedOutputStream.write(response.createHttpResponse().getBytes());
            bufferedOutputStream.flush();
        } catch (IOException | UncheckedServletException | NullPointerException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response branchRequest(final Request request) throws IOException {
        if (request.isFileRequest()) {
            return new FileRequestHandler().handle(request);
        }
        return new RequestHandler().handle(request);
    }
}
