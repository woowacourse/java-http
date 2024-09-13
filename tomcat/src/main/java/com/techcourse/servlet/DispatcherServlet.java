package com.techcourse.servlet;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.servlet.mapping.HandlerMapping;
import com.techcourse.servlet.mapping.RequestMappingHandlerMapping;
import com.techcourse.servlet.mapping.ResourceHandlerMapping;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;

public class DispatcherServlet {

    private static final DispatcherServlet INSTANCE = new DispatcherServlet();

    private final List<HandlerMapping> handlerMappings;


    private DispatcherServlet() {
        handlerMappings = new ArrayList<>();
        handlerMappings.add(new ResourceHandlerMapping());
        handlerMappings.add(new RequestMappingHandlerMapping());
    }

    public static DispatcherServlet getInstance() {
        return INSTANCE;
    }

    public void doDispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Servlet handler = findHandler(request);
        handler.doService(request, response);
    }

    private Servlet findHandler(HttpServletRequest request) {
        return handlerMappings.stream()
                .filter(handlerMapping -> handlerMapping.hasHandlerFor(request))
                .map(handlerMapping -> handlerMapping.getHandler(request))
                .findFirst()
                .orElseThrow(() -> new UncheckedServletException("요청을 처리할 수 있는 핸들러를 찾을 수 없습니다"));
    }
}
