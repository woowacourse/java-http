package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.view.ViewUtils;

public class StaticController implements Controller {

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".html", ".css", ".js", ".ico");

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return ALLOWED_EXTENSIONS.stream().anyMatch(path::endsWith);
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException, URISyntaxException {
        httpResponse.setHttpStatus(HttpStatus.OK);
        return ViewUtils.render(httpResponse, httpRequest.getPath());
    }
}
