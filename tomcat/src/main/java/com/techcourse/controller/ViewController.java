package com.techcourse.controller;

import com.techcourse.ResponseWriters;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resource.ResourceUtil;
import org.apache.coyote.http11.response.HttpResponse;

public class ViewController extends AbstractController {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String resourcePath = httpRequest.getMappingLine().getPath();
        URL url = ResourceUtil.resolve(resourcePath);
        byte[] body;
        try {
            body = ResourceUtil.readAll(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String contentType = URLConnection.guessContentTypeFromName(url.toString());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        int idx = contentType.indexOf("/");
        if (idx == -1) {
            contentType = "application/octet-stream";
        }
        ResponseWriters.ok(httpResponse, body, contentType);
    }
}
