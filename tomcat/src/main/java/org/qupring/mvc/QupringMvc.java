package org.qupring.mvc;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.file.HtmlReader;
import org.qupring.mvc.handler.HandlerMapping;

public class QupringMvc {

    private final HandlerMapping handlerMapping;

    public QupringMvc(HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public void run(HttpRequest request, HttpResponse response) {

        /*
        if(runController(request, response)) {
            return;
        }
         */

        if (runStaticResources(request, response)) {
            return;
        }

        response.setStatus(404);
        response.setBody("Not Found");
    }

    /*
    private boolean runController(HttpRequest request, HttpResponse response) {
        Method method = handlerMapping.getControllerMethod(request.getUrl(), request.getHttpMethod());
        if (method == null) {
            return false;
        }
        try {
            method.invoke(null, request, response);
            return true;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke controller method", e);
        }
    }
     */

    private boolean runStaticResources(HttpRequest request, HttpResponse response) {
        if (request.getUrl().equals("/")) {
            response.setBody("hello world");
            return true;
        }

        String filePath = handlerMapping.getResource(request.getUrl());

        if (filePath == null) {
            return false;
        }
        response.setBody(HtmlReader.read(filePath));
        response.setHeader(
                "Content-Type",
                contentType(filePath)
        );
        return true;
    }

    private String contentType(String path) {

        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }
}
