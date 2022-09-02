package org.apache.coyote.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(BasicController.class);
    
    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        String requestUri = httpRequest.getRequestUri();
        Map<String, String> queryParams = httpRequest.getQueryParams();
        Map<String, String> headers = httpRequest.getHttpHeaders();

        String filePath = getClass().getClassLoader().getResource("static" + requestUri).getFile();
        String responseBody = null;
        try {
            responseBody = new String(Files.readAllBytes(new File(filePath).toPath()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return new HttpResponse(headers.get("Accept"), responseBody);
    }
}
