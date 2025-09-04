package com.techcourse.handler;

import static org.apache.coyote.HttpStatus.NOT_FOUND;
import static org.apache.coyote.HttpStatus.OK;

import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpRequestHandler;
import org.apache.coyote.HttpResponse;

public class DefaultHandler implements HttpRequestHandler {

    private static final String STATIC_FILE_PATH_PREFIX = "static";

    @Override
    public void handleGet(HttpRequest request, HttpResponse response) {
        final URL resource = getClass().getClassLoader().getResource(STATIC_FILE_PATH_PREFIX + request.getPath());
        if (resource == null) {
            response.setStatus(NOT_FOUND);
            response.setBody("파일이 존재하지 않습니다.");
            response.setContentType("text/plain;charset=utf-8");
            return;
        }
        final File indexFile = new File(resource.getPath());

        try {
            final String contentType = Files.probeContentType(indexFile.toPath());
            final String responseBody = Files.readString(indexFile.toPath());

            response.setContentType(contentType + ";charset=utf-8");
            response.setBody(responseBody);

        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }

        response.setStatus(OK);
    }
}
