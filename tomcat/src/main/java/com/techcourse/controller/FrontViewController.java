package com.techcourse.controller;

import com.techcourse.exception.ResourceNotFoundException;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.util.FileReader;
import org.apache.coyote.util.HttpResponseBuilder;

public final class FrontViewController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse httpResponse) {
        try {
            String fileName = HttpRequestParser.getFilePathFromUri(request.getPath());
            List<String> contentLines = FileReader.readAllLines(fileName);
            HttpResponseBuilder.buildStaticContent(httpResponse, fileName, contentLines);
        } catch (ResourceNotFoundException e) {
            buildResponseToNotFound(httpResponse);
        }
    }

    private void buildResponseToNotFound(HttpResponse httpResponse) {
        String fileName = "404.html";
        List<String> contentLines = FileReader.readAllLines(fileName);
        HttpResponseBuilder.buildNotFound(httpResponse, contentLines);
    }
}
