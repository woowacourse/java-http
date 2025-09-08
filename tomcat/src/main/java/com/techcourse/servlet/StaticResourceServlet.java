package com.techcourse.servlet;

import com.techcourse.servlet.util.StaticFileLoader;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;

public class StaticResourceServlet extends HttpServlet {
    private static final String STATIC_DIR = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            String path = request.getRequestPath();
            byte[] content = StaticFileLoader.loadStaticFile(STATIC_DIR + path);
            response.setContentType(ContentType.getContentTypeFrom(path));
            response.appendToBody(content);
        } catch (Exception e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }
}
