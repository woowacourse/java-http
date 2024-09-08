package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import org.apache.ObjectMapper;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.handler.LoginRequestHandler;
import org.apache.coyote.handler.NotFoundHandler;
import org.apache.coyote.handler.RootRequestHandler;
import org.apache.coyote.handler.SignupRequestHandler;
import org.apache.coyote.handler.StaticResourceRequestHandler;
import org.apache.coyote.http11.request.Http11Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final List<RequestHandler> requestHandlers;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestHandlers = findImplementations();
    }

    private List<RequestHandler> findImplementations() {
        return List.of(
                new RootRequestHandler(),
                new LoginRequestHandler(),
                new SignupRequestHandler(),
                new StaticResourceRequestHandler(),
                new NotFoundHandler()
        );
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
            for (RequestHandler requestHandler : requestHandlers) {
                if (requestHandler.canHandling(request)) {
                    HttpResponse response = requestHandler.handle(request);
                    outputStream.write(ObjectMapper.serialize(response));
                    outputStream.flush();
                    break;
                }
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
