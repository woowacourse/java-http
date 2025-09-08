package org.apache.catalina.servlet;

import com.http.enums.HttpStatus;
import com.http.servlet.LoginServlet;
import com.http.servlet.RegisterServlet;
import com.techcourse.exception.HttpStatusException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.domain.request.HttpRequest;
import org.apache.catalina.domain.response.HttpResponse;
import org.apache.catalina.servlet.impl.DefaultServlet;
import org.apache.coyote.http11.ResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpServletContainer {

    private static final Logger log = LoggerFactory.getLogger(HttpServletContainer.class);

    private static final Map<String, HttpServlet> handlers = new HashMap<>();
    private static final HttpServlet defaultServlet = new DefaultServlet();

    private HttpServletContainer() {
    }

    static {
        handlers.put("/login", new LoginServlet());
        handlers.put("/register", new RegisterServlet());
    }

    public static void handle(HttpRequest request, HttpResponse response) throws IOException {
        final String path = request.requestStartLine().path();

        try {
            handlers.getOrDefault(path, defaultServlet).service(request, response);
        } catch (HttpStatusException e) {
            throw e;
        } catch (FileNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("서버 오류 발생 = {}", e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        processResponse(request, response);
    }

    private static void processResponse(HttpRequest request, HttpResponse response)
            throws IOException {
        if (response.getStatus().isError()) {
            log.debug("에러 페이지 접근 status : {}", response.getStatus());
            ResponseProcessor.handleErrorPage(request, response);
            return;
        }

        ResponseProcessor.handle(request, response);
    }
}
