package org.apache.catalina.handler;

import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javassist.NotFoundException;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.exception.ExceptionRenderer;
import org.apache.catalina.exception.GlobalExceptionHandler;
import org.apache.catalina.exception.GlobalExceptionHandler.ErrorPage;
import org.apache.coyote.http11.http.request.dto.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherHandler {

    private static final Logger log = LoggerFactory.getLogger(DispatcherHandler.class);

    private final List<HandlerMapping> handlerMappings;
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final ExceptionRenderer exceptionRenderer = new ExceptionRenderer();

    public DispatcherHandler(List<HandlerMapping> mappings) {
        this.handlerMappings = mappings.stream()
                .sorted(Comparator.comparing(HandlerMapping::getOrder))
                .toList();
    }

    public void dispatch(HttpRequest request, OutputStream outputStream) throws Exception {
        HttpResponse response = new HttpResponse(request.requestLine().version());

        try {
            Controller handler = findHandler(request);
            handler.service(request, response);
        } catch (Exception e) {
            log.error("Exception during request processing: {}", request.path(), e);
            ErrorPage errorPage = exceptionHandler.resolve(e);
            exceptionRenderer.render(response, errorPage);
        }
        response.commit(outputStream);
    }

    private Controller findHandler(HttpRequest request) throws NotFoundException {
        return handlerMappings.stream()
                .map(mapping -> mapping.getHandler(request))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("존재하지 않습니다."));
    }
}
