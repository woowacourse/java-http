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
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.request.HttpRequestParser;
import org.apache.coyote.web.response.HttpResponse;
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

            HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);
            HttpResponse httpResponse = branchRequest(httpRequest);

            bufferedOutputStream.write(httpResponse.createHttpResponse().getBytes());
            bufferedOutputStream.flush();
        } catch (IOException | UncheckedServletException | NullPointerException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse branchRequest(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isFileRequest()) {
            return new FileRequestHandler().handle(httpRequest);
        }
        return new RequestHandler().handle(httpRequest);
    }
}
