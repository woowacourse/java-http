package org.qupring.mvc;

import java.lang.reflect.Method;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.file.HtmlReader;
import org.qupring.mvc.handler.HandlerMapping;

public class QupringMvc {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String NOT_FOUND_PAGE = "static/404.html";

    private final HandlerMapping handlerMapping;

    public QupringMvc(HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public void run(HttpRequest request, HttpResponse response) {
        System.out.println("Request URL: " + request.getUrl() + ", Method: " + request.getHttpMethod());

        /*
        if (runController(request, response)) {
            return;
        }

         */

        if (runStaticResources(request, response)) {
            return;
        }

        response.setStatus(404);
        response.setBody(HtmlReader.read(NOT_FOUND_PAGE));
        response.setHeader("Content-Type", DEFAULT_CONTENT_TYPE);
    }

    /*
    private boolean runController(HttpRequest request, HttpResponse response) {
        Method method = handlerMapping.getControllerMethod(request.getUrl(), request.getHttpMethod());
        if (method == null) {
            return false;
        }
        try {
            Object controller = method
                    .getDeclaringClass()
                    .getDeclaredConstructor()
                    .newInstance(); // 실행가능 메소드 상태로 만듦

            Object result = method.invoke(controller, request, response);
            setControllerResponse(response, result);

            return true;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("컨트롤러 메소드를 실패했습니다.", e);
        }
    }

    private void setControllerResponse(HttpResponse response, Object result) {
        if (result instanceof String viewName) {
            String filePath = handlerMapping.getResource("/" + viewName + ".html");
            setResponse(response, filePath);
        }
    }


    private void setResponse(HttpResponse response, String filePath) {
        if (filePath != null) {
            response.setBody(HtmlReader.read(filePath));
            response.setHeader("Content-Type", contentType(filePath));
            return;
        }
        response.setStatus(404);
        response.setBody(HtmlReader.read(NOT_FOUND_PAGE));
    }

     */

    private boolean runStaticResources(HttpRequest request, HttpResponse response) {
        if (request.getUrl().equals("/")) {
            response.setBody("Hello world!");
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

        return DEFAULT_CONTENT_TYPE;
    }

}
