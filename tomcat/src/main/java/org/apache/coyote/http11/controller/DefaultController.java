package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusLine;

public class DefaultController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new RuntimeException();
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            HttpStatusLine httpStatusLine = new HttpStatusLine(httpRequest.getVersion(), HttpStatusCode.OK);

            String path = httpRequest.getPath();
            if (!httpRequest.getPath().contains(".")) {
                path += ".html";
            }
            String fileName = "static" + path;
            var resourceUrl = getClass().getClassLoader().getResource(fileName);
            if (resourceUrl == null) {
                resourceUrl = getClass().getClassLoader().getResource("static/404.html");
            }
            Path filePath = Path.of(resourceUrl.toURI());
            String responseBody = new String(Files.readAllBytes(filePath));

            return new HttpResponse.Builder(httpStatusLine)
                    .contentType(Files.probeContentType(filePath) + ";charset=utf-8")
                    .contentLength(String.valueOf(responseBody.getBytes().length))
                    .responseBody(responseBody)
                    .build();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
