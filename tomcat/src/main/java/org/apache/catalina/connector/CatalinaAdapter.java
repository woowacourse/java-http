package org.apache.catalina.connector;

import org.apache.catalina.mapping.Controller;
import org.apache.catalina.mapping.RequestMapping;
import org.apache.coyote.Adapter;
import org.apache.coyote.SessionManager;
import org.apache.coyote.util.StaticResourceHandler;
import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.response.HttpContentTypeResolver;
import org.apache.coyote.util.response.HttpResponse;
import org.apache.coyote.util.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalinaAdapter implements Adapter {

    private static final Logger log = LoggerFactory.getLogger(CatalinaAdapter.class);
    private final SessionManager sessionManager;

    public CatalinaAdapter(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request != null) {
            request.setSessionManager(sessionManager);
        }
        try {
            if (request == null) {
                createNotFoundResponse(response);
                return;
            }
            Controller controller = RequestMapping.getController(request.getPath());
            if (controller != null) {
                controller.service(request, response);
                return;
            }
            if (handleStaticResource(request, response)) {
                return;
            }
            createNotFoundResponse(response);
        } catch (Exception e) {
            log.error("service error: {}", e.getMessage(), e);
            createInternalServerErrorResponse(response);
        }
    }

    @Override
    public void handleError(HttpResponse response, HttpStatus status) {
        createErrorResponse(response, status);
    }

    private boolean handleStaticResource(HttpRequest request, HttpResponse response) {
        String resourcePath = "static" + request.getPath();
        byte[] body = StaticResourceHandler.readResource(resourcePath);
        if (body == null) {
            return false;
        }
        response.setStatus(HttpStatus.OK);
        response.addHeader("Content-Type", HttpContentTypeResolver.resolve(request.getPath()));
        response.setBody(body);
        return true;
    }

    private void createNotFoundResponse(HttpResponse response) {
        createErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    private void createInternalServerErrorResponse(HttpResponse response) {
        createErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void createErrorResponse(HttpResponse response, HttpStatus status) {
        response.setStatus(status);
        String resourcePath = "static/" + status.getCode() + ".html";
        byte[] body = StaticResourceHandler.readResource(resourcePath);
        if (body != null) {
            response.setBody(body);
            response.addHeader("Content-Type", "text/html;charset=utf-8");
        }
    }
}
