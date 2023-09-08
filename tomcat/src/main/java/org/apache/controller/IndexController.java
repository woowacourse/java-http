package org.apache.controller;

import nextstep.jwp.common.HttpHeader;
import nextstep.jwp.common.HttpRequest;
import nextstep.jwp.common.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static nextstep.jwp.common.StatusCode.OK;

public class IndexController extends AbstractController {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException {
        super.service(httpRequest, httpResponse);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException {
        Path path = readPath(httpRequest.getHttpLine().getUrl());
        byte[] bytes = readBytes(path);
        String responseBody = new String(bytes);
        HttpHeader httpResponseHeader = HttpHeader.from(List.of(
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length));

        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode(OK);
        httpResponse.setHttpHeader(httpResponseHeader);
        httpResponse.setResponseBody(responseBody);
    }

    private Path readPath(String url) throws URISyntaxException {
        URL resourcePath = getClass().getClassLoader().getResource("static" + url);
        return Path.of(resourcePath.toURI());
    }

    private byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }
}
