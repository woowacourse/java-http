package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.request.HttpHeader;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class AbstractController implements Controller {

    private static final String FILE_EXTENSION_SEPARATOR_REGEX = "\\.";
    private static final String DEFAULT_FILE_EXTENSION = ".html";
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method = request.parseRequestLine().getMethod();
        if (method.isPost()) {
            doPost(request, response);
            return;
        }
        if (method.isGet()) {
            doGet(request, response);
        }
    }

    protected void redirectTo(HttpResponse response, String location) throws IOException {
        response.addStatusLine(HttpStatusCode.FOUND);
        response.addHeader(HttpHeader.LOCATION, location);
        response.writeResponse();
    }

    protected void serveStaticFile(HttpRequest request, HttpResponse response) throws IOException {
        String path = addDefaultExtensionIfMissing(request.getPath());
        String body = getStaticFileContent(path);
        if (body == null) {
            String notFoundPage = getStaticFileContent("/404.html");
            response.addStatusLine(HttpStatusCode.OK);
            response.addHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
            response.addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(notFoundPage.getBytes().length));
            response.addBody(notFoundPage);
            response.writeResponse();
            return;
        }
        response.addStatusLine(HttpStatusCode.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE, "text/" + getFileExtension(path) + ";charset=utf-8");
        response.addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.addBody(body);
        response.writeResponse();
    }

    private String addDefaultExtensionIfMissing(String path) {
        if (!path.contains(FILE_EXTENSION_SEPARATOR)) {
            return path + DEFAULT_FILE_EXTENSION;
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
