package org.apache.catalina.servlet;

import com.http.enums.HttpStatus;
import com.http.servlet.LoginServlet;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.servlet.impl.DefaultServlet;
import org.apache.coyote.http11.ResponseProcessor;

public final class HttpServletContainer {

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
        try{
            handlers.getOrDefault(path, defaultServlet).handle(request, response);
        }catch (IOException | UncheckedServletException e){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }catch (IllegalArgumentException e){
            status = HttpStatus.BAD_REQUEST;
        }

        ResponseProcessor.handle(request, response, status);
    }
}
