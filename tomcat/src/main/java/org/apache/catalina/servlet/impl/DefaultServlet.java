package org.apache.catalina.servlet.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.catalina.util.FileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultServlet implements HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DefaultServlet.class);

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        final String path = request.requestStartLine().path();

        if (!FileParser.existsFile(path)) {
            log.error("해당 경로에 파일이 존재하지 않습니다. path: {}", path);
            throw new FileNotFoundException("파일을 찾을 수 없습니다.");
        }

        final byte[] responseBody = FileParser.loadStaticResource(path);

        response.setBody(responseBody);
    }
}
