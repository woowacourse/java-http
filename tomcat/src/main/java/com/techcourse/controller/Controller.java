package com.techcourse.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.startline.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private static final String DELIMITER = "\r\n";

    public abstract boolean service(HttpRequest request, HttpResponse response);

    protected void redirectTo(HttpResponse response, String target) {
        response.setStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeader.LOCATION, target);
    }

    protected boolean responseResource(HttpResponse response, Path path) {
        try {
            String responseBody = readResource(path);
            String contentType = Files.probeContentType(path);
            response.addHeader(HttpHeader.CONTENT_TYPE, contentType + ";charset=utf-8");
            response.setBody(responseBody);
            return response.isValid();
        } catch (NullPointerException | IOException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("invalid path: " + path.toString());
        }
    }

    private String readResource(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }

        List<String> fileLines = Files.readAllLines(path);
        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (String fileLine : fileLines) {
            joiner.add(fileLine);
        }
        joiner.add("");
        return joiner.toString();
    }
}