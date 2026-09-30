package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor {

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
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
