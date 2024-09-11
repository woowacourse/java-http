package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isPostMethod()) {
            doPost(request, response);
            return;
        }
        if (request.isGetMethod()) {
            doGet(request, response);
        }
    }

    protected void redirectTo(HttpResponse response, String location) throws IOException {
        response.addStatusLine("HTTP/1.1 302 Found");
        response.addHeader("Location", "http://localhost:8080" + location);
        response.writeResponse();
    }

    protected void serveStaticFile(HttpRequest request, HttpResponse response) throws IOException {
        String path = addHtmlExtension(request.getPath());
        String body = getStaticFileContent(path);

        response.addStatusLine("HTTP/1.1 200 OK");
        response.addHeader("Content-Type", "text/" + getFileExtension(path) + ";charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        response.addBody(body);
        response.writeResponse();
    }

    private String addHtmlExtension(String path) {
        if (!"/".equals(path) && !path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private String getStaticFileContent(String path) throws IOException {
        if (Objects.equals(path, "/")) {
            return "Hello world!";
        }
        String staticPath = "static" + path;
        File file = new File(getClass().getClassLoader().getResource(staticPath).getPath());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getFileExtension(String path) {
        if (Objects.equals(path, "/")) {
            return "html";
        }
        String[] splitPath = path.split("\\.");
        return splitPath[splitPath.length - 1];
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}
}
