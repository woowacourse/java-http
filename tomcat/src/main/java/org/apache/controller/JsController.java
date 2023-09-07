package org.apache.controller;

import org.apache.common.HttpHeader;
import org.apache.common.HttpRequest;
import org.apache.common.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.apache.common.StatusCode.OK;

public class JsController extends AbstractController {
    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException {
        Path path = readPath(httpRequest.getHttpLine().getUrl());
        byte[] bytes = readBytes(path);
        String responseBody = new String(bytes);
        HttpHeader httpResponseHeader = HttpHeader.from(List.of(
                "Content-Type: Application/javascript;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length));

        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode(OK);
        httpResponse.setHttpHeader(httpResponseHeader);
        httpResponse.setResponseBody(new String(bytes));
    }

    private Path readPath(String url) throws URISyntaxException {
        URL resourcePath = getClass().getClassLoader().getResource("static" + url);
        return Path.of(resourcePath.toURI());
    }

    private byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }
}
