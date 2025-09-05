package org.apache.catalina.servlet;

import com.http.enums.HttpStatus;
import com.http.servlet.LoginServlet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
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
    }

    public static void handle(HttpRequest request, HttpResponse response) throws IOException {
        final String path = request.requestStartLine().path();
        HttpStatus status = HttpStatus.OK;

        try {
            handlers.getOrDefault(path, defaultServlet).handle(request, response);
        } catch (FileNotFoundException e) {
            status = HttpStatus.NOT_FOUND;
        } catch (IllegalArgumentException e) {
            status = HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        processResponse(request, response, status);
    }

    private static void processResponse(HttpRequest request, HttpResponse response, HttpStatus status)
            throws IOException {
        if (status != HttpStatus.OK) {
            ResponseProcessor.handleErrorPage(response, status);
            return;
        }

        ResponseProcessor.handle(request, response, status);
    }
}
