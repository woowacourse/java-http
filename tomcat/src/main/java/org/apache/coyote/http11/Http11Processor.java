package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.adapter.HttpRequestAdapter;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final HttpRequestAdapter requestAdapter;
    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
        this.requestAdapter = new HttpRequestAdapter();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final HttpRequest request = new HttpRequest(inputStream);

            final HttpResponse response = requestAdapter.service(request, PROTOCOL_VERSION);

            writeResponse(response, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(HttpResponse response, OutputStream outputStream) throws IOException {
        outputStream.write(response.getResponse().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
