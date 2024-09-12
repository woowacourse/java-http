package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
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
        try (OutputStream outputStream = connection.getOutputStream();
             InputStream inputStream = connection.getInputStream()) {
            Http11Request request = Http11Request.from(inputStream);
            Http11Response response = Http11Response.of(request);
            Http11RequestHandler requestHandler = Http11RequestHandler.from();
            requestHandler.handle(request, response);
            writeAndFlush(outputStream, response.getResponse());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void writeAndFlush(OutputStream outputStream, String responseString) throws IOException {
        outputStream.write(responseString.getBytes());
        outputStream.flush();
    }
}
