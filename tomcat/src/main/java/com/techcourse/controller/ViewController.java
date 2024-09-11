package com.techcourse.controller;

import com.techcourse.exception.ResourceNotFoundException;
import com.techcourse.session.SessionManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.util.FileReader;
import org.apache.coyote.util.HttpResponseBuilder;

public final class ViewController extends AbstractController {

    @Override
    public void requestMapping(HttpRequest request, HttpResponse httpResponse) {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, httpResponse);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, httpResponse);
        }
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse httpResponse) {
        Cookies cookies = request.getCookies();
        if (request.getUri().equals("/login")
                && cookies.hasJSESSIONID()
                && SessionManager.isRegisitered(cookies.getJSESSIONID())) {
            HttpResponseBuilder.setRedirection(httpResponse, "/");
            return;
        }

        try {
            String fileName = getFilePath(request.getPath());
            Path filePath = FileReader.parseFilePath(fileName);
            List<String> contentLines = FileReader.readAllLines(filePath);
            HttpResponseBuilder.buildStaticContent(httpResponse, fileName, contentLines);
        } catch (ResourceNotFoundException | URISyntaxException | IOException e) {
            notFound(httpResponse);
        }
    }

    private String getFilePath(String path) {
        if (path.isEmpty()) {
            return "index.html";
        }
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private void notFound(HttpResponse httpResponse) {
        String fileName = getFilePath("404.html");

        try {
            Path filePath = FileReader.parseFilePath(fileName);
            List<String> contentLines = FileReader.readAllLines(filePath);
            HttpResponseBuilder.buildNotFound(httpResponse, contentLines);
        } catch (URISyntaxException | IOException e) {
            throw new ResourceNotFoundException(fileName);
        }
    }
}
