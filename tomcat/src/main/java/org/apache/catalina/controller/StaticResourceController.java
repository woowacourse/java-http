package org.apache.catalina.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpResponseHeader;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.StaticResourceExtension;
import org.apache.coyote.http11.message.response.StatusLine;

public class StaticResourceController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();

        if (!StaticResourceExtension.anyMatch(path)) {
            path = path + ".html";
        }
        URL resource = getPathOfResource(path);
        String responseBody = readFile(resource);

        StatusLine statusLine = new StatusLine(HttpStatus.OK, request);

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add("Content-Type",
                StaticResourceExtension.findMimeTypeByUrl(path) + ";charset=utf-8");
        httpResponseHeader.add("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));

        response.setStatusLine(statusLine);
        response.setHttpResponseHeader(httpResponseHeader);
        response.setResponseBody(responseBody);
    }

    private static String readFile(URL resource) throws IOException {
        File file = new File(resource.getFile());
        return Files.readString(file.toPath());
    }

    private URL getPathOfResource(String uri) {
        URL resource = getClass().getClassLoader().getResource("static/" + uri);
        if (resource != null) {
            return resource;
        }

        throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
    }
}
