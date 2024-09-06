package com.techcourse.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.ResponseBody;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.exception.UnsupportedMethodException;

public class NotFoundController extends Controller {
    private static final NotFoundController instance = new NotFoundController();

    private NotFoundController() {
    }

    public static NotFoundController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        String fileName = "404.html";
        ResponseBody responseBody = new ResponseBody(getResponseBody(fileName));
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setContentType(MimeType.HTML);
        response.setBody(responseBody);
        return response;
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }

    private String getResponseBody(String fileName) throws IOException {
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

