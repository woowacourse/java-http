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
import nextstep.jwp.httpserver.domain.Cookie;
import nextstep.jwp.httpserver.domain.HttpSession;
import nextstep.jwp.httpserver.domain.HttpSessions;
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

            doSessionFilter(httpRequest);

            final Object handler = getHandler(httpRequest);
            final HandlerAdapter handlerAdapter = getHandlerAdapter(handler);

            final ModelAndView mv = handlerAdapter.handle(httpRequest, httpResponse, handler);
            render(mv, httpResponse);
            commitSession(httpRequest, httpResponse);

            outputStream.write(httpResponse.getResult().getBytes());
            outputStream.flush();
        } catch (Exception exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void doSessionFilter(HttpRequest httpRequest) {
        final String sessionId = httpRequest.sessionIdInCookie();
        final HttpSession session = HttpSessions.getSession(sessionId);
        httpRequest.setSession(session);
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

    private void render(ModelAndView mv, HttpResponse httpResponse) throws IOException, URISyntaxException {
        final View view = resolve(mv);
        view.render(httpResponse);
    }

    private View resolve(ModelAndView mv) {
        if (mv.isResourceFile()) {
            return new View("static" + mv.getViewName());
        }
        return new View("static" + mv.getViewName() + ".html");
    }

    private void commitSession(HttpRequest httpRequest, HttpResponse httpResponse) {
        final HttpSession originalSession = HttpSessions.getSession(httpRequest.getSessionId());
        originalSession.invalidate();
        final HttpSession session = httpRequest.getSession();
        HttpSessions.save(session);

        addCookie(httpRequest, httpResponse);
    }

    private void addCookie(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (!httpRequest.hasSessionId()) {
            final Cookie cookie = new Cookie("JSESSIONID", httpRequest.getSessionId());
            httpResponse.addCookie(cookie);
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
