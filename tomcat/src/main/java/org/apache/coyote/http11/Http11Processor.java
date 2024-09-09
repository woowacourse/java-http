package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.ObjectMapper;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.RequestHandlerMapping;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestHandlerMapping requestHandlerMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestHandlerMapping = new RequestHandlerMapping();
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

            final Http11Request request = ObjectMapper.deserialize(bufferedReader);

            RequestHandler requestHandler = requestHandlerMapping.getRequestHandler(request);
            HttpResponse response = new Http11Response();
            requestHandler.handle(request, response);

            outputStream.write(ObjectMapper.serialize(response));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
