package org.apache.controller;

import nextstep.jwp.common.ContentType;
import nextstep.jwp.common.HttpHeader;
import nextstep.jwp.common.HttpRequest;
import nextstep.jwp.common.HttpResponse;
import nextstep.jwp.common.Reader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static nextstep.jwp.common.StatusCode.NOT_FOUND;
import static nextstep.jwp.common.StatusCode.OK;

public class FileController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException {
        Path path = readPath(httpRequest.getHttpLine().getUrl());
        byte[] bytes = Reader.readBytes(path);
        if (bytes == null) {
            httpResponse.setVersion("HTTP/1.1");
            httpResponse.setStatusCode(NOT_FOUND);
            return;
        }
        setResponse(httpRequest, httpResponse, bytes);
    }

    private void setResponse(HttpRequest httpRequest, HttpResponse httpResponse, byte[] bytes) {
        String responseBody = new String(bytes);
        ContentType contentType = ContentType.from(httpRequest.getHttpLine().getUrl());
        HttpHeader httpResponseHeader = HttpHeader.from(List.of(
                "Content-Type: " + contentType.getContentType() + ";charset=utf-8",
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

    private byte[] readBytes(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            return bytes;
        } catch (IOException e) {
        }
        return null;
    }
}
