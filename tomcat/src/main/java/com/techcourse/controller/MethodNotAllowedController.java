package com.techcourse.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.response.ResponseBody;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.exception.UnsupportedMethodException;

public class MethodNotAllowedController extends Controller {
    private static final MethodNotAllowedController instance = new MethodNotAllowedController();

    private MethodNotAllowedController() {
    }

    public static MethodNotAllowedController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        String fileName = "405.html";
        ResponseBody responseBody = new ResponseBody(readResource(fileName));
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.setContentType(MimeType.HTML);
        response.setBody(responseBody);
        return response;
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: POST");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: GET");
    }

    private String readResource(String fileName) throws IOException {
        URL resource = findResource(fileName);
        if (Objects.isNull(resource)) {
            throw new UncheckedServletException("Cannot find resource with name: " + fileName);
        }
        Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private URL findResource(String fileName) {
        return getClass().getClassLoader().getResource("static/" + fileName);
    }
}
