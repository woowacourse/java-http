package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Router router = new Router();

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        //TODO: 프로세스 메서드에서도 try-catch를 없앨 수 있을까 ?
        try (final var inputStream = new Http11InputBuffer(connection.getInputStream());
             final var outputStream = new Http11OutputBuffer(connection.getOutputStream())) {
            HttpRequest httpRequest = new HttpRequestParser().parse(inputStream);
            HttpResponse httpResponse = new HttpResponse();

            router.handle(httpRequest, httpResponse);
            outputStream.commitAndWrite(httpResponse);

        } catch (IOException e) {
            log.error("IO error during request processing", e);
        }
    }
}
