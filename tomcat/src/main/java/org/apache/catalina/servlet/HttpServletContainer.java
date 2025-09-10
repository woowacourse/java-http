package org.apache.catalina.servlet;

import com.spring.http.enums.HttpStatus;
import com.spring.http.request.HttpRequest;
import com.spring.http.response.HttpResponse;
import com.spring.servlet.DispatcherServlet;
import com.techcourse.exception.HttpStatusException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.servlet.impl.DefaultServlet;
import org.apache.coyote.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpServletContainer {

    private static final Logger log = LoggerFactory.getLogger(HttpServletContainer.class);

    private static final Map<String, HttpServlet> handlers = new HashMap<>();
    private static final HttpServlet defaultServlet = new DefaultServlet();

    private HttpServletContainer() {
    }

    static {
        final HttpServlet dispatcherServlet = new DispatcherServlet();
        handlers.put("/login", dispatcherServlet);
        handlers.put("/register", dispatcherServlet);
    }

    public static void handle(HttpRequest request, HttpResponse response) throws IOException {
        final String path = request.requestStartLine().path();

        try {
            handlers.getOrDefault(path, defaultServlet).service(request, response);
        } catch (FileNotFoundException e) {
            throw new HttpStatusException(e, HttpStatus.BAD_REQUEST);
        }

        processResponse(request, response);
    }

    private static void processResponse(HttpRequest request, HttpResponse response)
            throws IOException {
        if (response.getStatus().isError()) {
            log.debug("에러 페이지 접근 status : {}", response.getStatus());
            ResponseUtil.handleErrorPage(request, response);
            return;
        }

        ResponseUtil.handle(request, response);
    }
}
