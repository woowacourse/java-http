package com.techcourse.servlet;

import com.techcourse.exception.NoResourceFoundException;
import com.techcourse.servlet.util.StaticFileLoader;
import java.io.IOException;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;

public class ServletExceptionHandler {
    public static ServletExceptionHandler INSTANCE = new ServletExceptionHandler();

    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String INTERNAL_SERVER_ERROR_PAGE = "static/500.html";

    public static ServletExceptionHandler getInstance() {
        return INSTANCE;
    }

    public void handle(HttpResponse response, Throwable throwable) {
        if (throwable instanceof NoResourceFoundException) {
            send404(response);
            return;
        }
        send500(response);
    }

    private void send404(HttpResponse response) {
        try {
            byte[] content = StaticFileLoader.loadStaticFile(NOT_FOUND_PAGE);
            response.init();
            response.setContentType(ContentType.getContentTypeFrom(NOT_FOUND_PAGE));
            response.appendToBody(content);

        } catch (IOException e) {
            send500(response);
        }
    }

    private void send500(HttpResponse response) {
        try {
            byte[] content = StaticFileLoader.loadStaticFile(INTERNAL_SERVER_ERROR_PAGE);
            response.init();
            response.setContentType(ContentType.getContentTypeFrom(INTERNAL_SERVER_ERROR_PAGE));
            response.appendToBody(content);

        } catch (IOException e) {
            // 재귀 없이 간단한 텍스트 응답으로 fallback
            response.init();
            response.setContentType(ContentType.PLAIN);
            response.appendToBody("500 Internal Server Error".getBytes());
        }
    }
}
