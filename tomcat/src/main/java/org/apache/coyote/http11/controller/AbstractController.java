package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.request.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String FILE_EXTENSION_SEPARATOR_REGEX = "\\.";

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
        response.addHeader(HttpHeader.LOCATION, "http://localhost:8080" + location);
        response.writeResponse();
    }

    protected void serveStaticFile(HttpRequest request, HttpResponse response) throws IOException {
        String path = addHtmlExtension(request.getPath());
        String body = getStaticFileContent(path);
        if (body == null) {
            response.addStatusLine("HTTP/1.1 204 No Content");
            response.addHeader(HttpHeader.CONTENT_LENGTH, "0");
            response.writeResponse();
            return;
        }
        response.addStatusLine("HTTP/1.1 200 OK");
        response.addHeader(HttpHeader.CONTENT_TYPE, "text/" + getFileExtension(path) + ";charset=utf-8");
        response.addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.addBody(body);
        response.writeResponse();
    }

    private String addHtmlExtension(String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private String getStaticFileContent(String path) throws IOException {
        String staticPath = "static" + path;
        URL url = getClass().getClassLoader().getResource(staticPath);
        if (url == null) {
            return null;
        }
        File file = new File(url.getPath());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getFileExtension(String path) {
        String[] splitPath = path.split(FILE_EXTENSION_SEPARATOR_REGEX);
        return splitPath[splitPath.length - 1];
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}
}
