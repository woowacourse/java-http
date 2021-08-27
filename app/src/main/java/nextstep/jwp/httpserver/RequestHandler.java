package nextstep.jwp.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import nextstep.jwp.httpserver.adapter.HandlerAdapter;
import nextstep.jwp.httpserver.domain.View;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.mapping.HandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private List<HandlerMapping> handlerMappings;
    private List<HandlerAdapter> handlerAdapters;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
        initHandlerMappings();
        initHandlerAdapters();
    }

    private void initHandlerMappings() {
        Map<String, HandlerMapping> beans = BeanFactory.findByClassType(HandlerMapping.class);
        handlerMappings = new ArrayList<>(beans.values());
    }

    private void initHandlerAdapters() {
        Map<String, HandlerAdapter> beans = BeanFactory.findByClassType(HandlerAdapter.class);
        handlerAdapters = new ArrayList<>(beans.values());
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequestParser.parse(inputStream);
            Object handler = getHandler(httpRequest);
            HandlerAdapter handlerAdapter = getHandlerAdapter(handler);
            View view = handlerAdapter.handle(handler);

            outputStream.write(view.getContent().getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }


    private Object getHandler(HttpRequest httpRequest) {
        return handlerMappings.stream()
                              .filter(m -> m.isHandle(httpRequest))
                              .findFirst()
                              .map(m -> m.find(httpRequest))
                              .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 요청입니다."));
    }

    private HandlerAdapter getHandlerAdapter(Object handler) {
        return handlerAdapters.stream()
                              .filter(adapter -> adapter.supports(handler))
                              .findFirst()
                              .orElseThrow(() -> new IllegalArgumentException("처리할 수 있는 어댑터가 없습니다."));
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
