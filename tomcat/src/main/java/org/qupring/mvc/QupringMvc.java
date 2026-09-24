package org.qupring.mvc;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.file.HtmlReader;
import org.qupring.mvc.controller.Controller;
import org.qupring.mvc.handler.RequestMapping;

public class QupringMvc {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String NOT_FOUND_PAGE = "static/404.html";

    private final RequestMapping requestMapping;

    public QupringMvc(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public void run(HttpRequest request, HttpResponse response) {
        System.out.println("Request URL: " + request.getUrl() + ", Method: " + request.getHttpMethod());

        if (runController(request, response)) {
            return;
        }

        if (runStaticResources(request, response)) {
            return;
        }

        response.setStatus(404);
        response.setBody(HtmlReader.read(NOT_FOUND_PAGE));
        response.setHeader("Content-Type", DEFAULT_CONTENT_TYPE);
    }

    private boolean runController(HttpRequest request, HttpResponse response) {
        Controller controller = requestMapping.getController(request);
        if (controller == null) {
            return false;
        }

        try {
            controller.service(request, response);
            return true;
        } catch (Exception exception) {
            throw new RuntimeException("컨트롤러를 실행하지 못했습니다.", exception);
        }
    }

    private boolean runStaticResources(HttpRequest request, HttpResponse response) {
        if (request.getUrl().equals("/")) {
            response.setBody("Hello world!");
            return true;
        }

        String filePath = requestMapping.getResource(request.getUrl());

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
