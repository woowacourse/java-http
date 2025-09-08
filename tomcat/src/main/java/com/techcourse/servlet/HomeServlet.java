package com.techcourse.servlet;

import com.techcourse.servlet.util.StaticFileLoader;
import java.io.IOException;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HomeServlet extends HttpServlet {
    private static final String HOME_PAGE = "static/index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            byte[] content = StaticFileLoader.loadStaticFile(HOME_PAGE);
            response.setContentType(ContentType.getContentTypeFrom(HOME_PAGE));
            response.appendToBody(content);
        } catch (IOException e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }
}
