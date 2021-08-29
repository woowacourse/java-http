package nextstep.jwp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import nextstep.jwp.context.ApplicationContext;
import nextstep.jwp.dispatcher.adapter.HandlerAdapter;
import nextstep.jwp.dispatcher.mapping.HandlerMapping;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpResponseImpl;
import nextstep.jwp.http.parser.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    private final ApplicationContext applicationContext;

    private final List<HandlerMapping> handlerMappings;

    private final List<HandlerAdapter> handlerAdapters;

    public RequestHandler(Socket connection, ApplicationContext applicationContext,
        List<HandlerMapping> handlerMappings,
        List<HandlerAdapter> handlerAdapters) {
        this.connection = connection;
        this.applicationContext = applicationContext;
        this.handlerMappings = handlerMappings;
        this.handlerAdapters = handlerAdapters;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = HttpParser.parse(inputStream);
            request.setApplicationContext(applicationContext);
            HttpResponse response = new HttpResponseImpl();

            if (log.isDebugEnabled()) {
                log.debug("\r\n============== request ==============\r\n{}", request.asString());
            }

            HandlerMapping mappedHandler = getHandler(request);

            HandlerAdapter handlerAdapter = getHandlerAdapter(mappedHandler);

            handlerAdapter.handle(request, response, mappedHandler.getHandler(request));

            if (log.isDebugEnabled()) {
                log.debug("\r\n============== response ==============\r\n{}", response.asString());
            }

            outputStream.write(response.asString().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private HandlerMapping getHandler(HttpRequest request) {
        return this.handlerMappings
            .stream()
            .filter(handlerMapping -> handlerMapping.supports(request))
            .findFirst()
            .orElseThrow();
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping mappedHandler) {
        return this.handlerAdapters
            .stream()
            .filter(handlerAdapter -> handlerAdapter.supports(mappedHandler))
            .findFirst()
            .orElseThrow();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
