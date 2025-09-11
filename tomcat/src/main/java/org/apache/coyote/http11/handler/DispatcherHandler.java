package org.apache.coyote.http11.handler;

import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.handler.ready.HandlerMapping;
import org.apache.coyote.http11.request.dto.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DispatcherHandler {

    private final List<HandlerMapping> handlerMappings;

    public DispatcherHandler(List<HandlerMapping> mappings) {
        this.handlerMappings = mappings.stream()
                .sorted(Comparator.comparing(HandlerMapping::getOrder))
                .toList();
    }

    public void dispatch(HttpRequest request, OutputStream outputStream) throws Exception {
        HttpResponse response = new HttpResponse(request.version());

        for (HandlerMapping mapping : handlerMappings) {
            Controller handler = mapping.getHandler(request);
            if (handler != null) {
                handler.service(request, response);
                break;
            }
        }

        response.commit(outputStream);
    }
}
