package org.apache.coyote.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    public void process(RequestHandler requestHandler) {
        try {
            var request = readRequest();
            var response = requestHandler.handle(request);
            writeResponse(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public HttpRequest readRequest() throws IOException {
        return HttpRequest.parse(connection.getInputStream());
    }

    public void writeResponse(HttpResponse response) throws IOException {
        var outputStream = connection.getOutputStream();
        outputStream.write(response.toHttpMessage().getBytes());
        outputStream.flush();
    }
}
