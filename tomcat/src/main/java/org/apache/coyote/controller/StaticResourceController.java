package org.apache.coyote.controller;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws Exception {
        response.setStatusCode(StatusCode.OK);
        response.setContentType(request.getContentType());
        final var responseBody = readStaticResource(request, "Hello world!");
        response.writeBody(responseBody);
    }

    private static String readStaticResource(MyHttpRequest httpRequest, String defaultContent)
            throws IOException, URISyntaxException {
        URL fileUrl = Http11Processor.class
                .getClassLoader()
                .getResource(httpRequest.getResourcePath());
        File file = new File(Objects.requireNonNull(fileUrl).toURI());
        if (file.isFile()) {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        }
        return defaultContent;
    }
}
