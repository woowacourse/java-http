package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.ObjectMapper;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.RequestHandlerMapper;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestHandlerMapper requestHandlerMapper;

    public Http11Processor(final Socket connection) {
        this(connection, new RequestHandlerMapper());
    }

    public Http11Processor(final Socket connection, final RequestHandlerMapper requestHandlerMapper) {
        this.connection = connection;
        this.requestHandlerMapper = requestHandlerMapper;
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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest request = ObjectMapper.deserialize(bufferedReader);

            RequestHandler requestHandler = requestHandlerMapper.getRequestHandler(request);
            HttpResponse response = new Http11Response();
            requestHandler.handle(request, response);

            outputStream.write(ObjectMapper.serialize(response));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
