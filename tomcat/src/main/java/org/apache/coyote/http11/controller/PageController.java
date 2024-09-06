package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpResponseBody;
import org.apache.coyote.http11.httpresponse.HttpResponseHeader;
import org.apache.coyote.http11.httpresponse.HttpStatusLine;

public class PageController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        return null;
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
            Path filePath = Path.of(resourceUrl.toURI());
            String responseBody = new String(Files.readAllBytes(filePath));
            HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
            httpResponseHeader.addHeaders("Content-Type", ContentType.getContentType(path.split("\\.")[1]).getContentType());
            httpResponseHeader.addHeaders("Content-Length", String.valueOf(responseBody.getBytes().length));
            HttpResponseBody httpResponseBody = new HttpResponseBody(responseBody);

            return new HttpResponse(httpStatusLine, httpResponseHeader, httpResponseBody);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
