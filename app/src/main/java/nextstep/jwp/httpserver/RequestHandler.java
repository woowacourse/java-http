package nextstep.jwp.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.*;

import nextstep.jwp.httpserver.adapter.HandlerAdapter;
import nextstep.jwp.httpserver.domain.Cookie;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import nextstep.jwp.httpserver.domain.view.ModelAndView;
import nextstep.jwp.httpserver.domain.view.View;
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
            final HttpResponse httpResponse = new HttpResponse();

            final Object handler = getHandler(httpRequest);
            final HandlerAdapter handlerAdapter = getHandlerAdapter(handler);

            preHandle(httpRequest, httpResponse);

            final ModelAndView mv = handlerAdapter.handle(httpRequest, httpResponse, handler);
            render(mv, httpResponse, outputStream);
        } catch (Exception exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Object getHandler(HttpRequest httpRequest) {
        return handlerMappings.stream()
                              .filter(m -> m.canUse(httpRequest))
                              .findFirst()
                              .map(m -> m.getHandler(httpRequest))
                              .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 요청입니다."));
    }

    private HandlerAdapter getHandlerAdapter(Object handler) {
        return handlerAdapters.stream()
                              .filter(adapter -> adapter.supports(handler))
                              .findFirst()
                              .orElseThrow(() -> new IllegalArgumentException("처리할 수 있는 어댑터가 없습니다."));
    }

    private void preHandle(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpRequest.getCookies()
                   .forEach(httpResponse::addCookie);
        if (!httpRequest.hasSessionId()) {
            Cookie cookie = new Cookie("JSESSIONID", UUID.randomUUID().toString());
            httpResponse.addCookie(cookie);
        }
    }

    private void render(ModelAndView mv, HttpResponse httpResponse, OutputStream outputStream) throws IOException, URISyntaxException {
        View view = resolve(mv);
        String result = view.render(httpResponse);
        outputStream.write(result.getBytes());
        outputStream.flush();
    }

    private View resolve(ModelAndView mv) {
        if (mv.isResourceFile()) {
            return new View("static" + mv.getViewName());
        }
        return new View("static" + mv.getViewName() + ".html");
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
